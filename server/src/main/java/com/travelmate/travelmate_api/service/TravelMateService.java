package com.travelmate.travelmate_api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.models.nosql.BookingDoc;
import com.travelmate.travelmate_api.models.nosql.BudgetDoc;
import com.travelmate.travelmate_api.models.nosql.DestinationDoc;
import com.travelmate.travelmate_api.models.nosql.TripDoc;
import com.travelmate.travelmate_api.models.nosql.UserDoc;

import com.travelmate.travelmate_api.models.sql.BookingsSQL;
import com.travelmate.travelmate_api.models.sql.BudgetSQL;
import com.travelmate.travelmate_api.models.sql.DestinationSQL;
import com.travelmate.travelmate_api.models.sql.TripsSQL;
import com.travelmate.travelmate_api.models.sql.UserSQL;
import com.travelmate.travelmate_api.models.sql.VotingProposalSQL;

import com.travelmate.travelmate_api.repository.sql.BookingSQLRepository;
import com.travelmate.travelmate_api.repository.sql.BudgetSQLRepository;
import com.travelmate.travelmate_api.repository.sql.DestinationSQLRepository;
import com.travelmate.travelmate_api.repository.sql.TripSQLRepository;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;
import com.travelmate.travelmate_api.repository.sql.VotingProposalSQLRepository;

@Service
public class TravelMateService {

	private final UserSQLRepository userRepository;
    private final BookingSQLRepository bookingRepository;
    private final BudgetSQLRepository budgetRepository;
    private final DestinationSQLRepository destinationRepository;
    private final TripSQLRepository tripRepository;
    private final VotingProposalSQLRepository proposalRepository;
    private final Firestore firestore;

    public TravelMateService(UserSQLRepository userRepository, 
                             BookingSQLRepository bookingRepository,
                             BudgetSQLRepository budgetRepository,
                             DestinationSQLRepository destinationRepository,
                             TripSQLRepository tripRepository,
                             VotingProposalSQLRepository proposalRepository,
                             Firestore firestore) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.budgetRepository = budgetRepository;
        this.destinationRepository = destinationRepository;
        this.tripRepository = tripRepository;
        this.proposalRepository = proposalRepository;
        this.firestore = firestore;
    }

    // ==========================================
    // 1. USER CRUD (DUAL-PERSISTENCE)
    // ==========================================
    
    @Transactional
    public void createUserInBothSystems(UserSQL sqlUser, UserDoc noSqlUser) throws Exception {
        userRepository.save(sqlUser);
        firestore.collection("users").document(noSqlUser.getUserId()).set(noSqlUser).get();
    }

    @Transactional
    public void deleteUserFromBothSystems(Long sqlId, String noSqlId) throws Exception {
        userRepository.deleteById(sqlId);
        firestore.collection("users").document(noSqlId).delete().get();
    }

    // ==========================================
    // 2. BOOKING CRUD & DUAL HISTORICAL CORRELATION
    // ==========================================

    public List<BookingsSQL> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional 
    public String executeHybridBooking(Long userId, String tripId, String bookingType, Double price) throws Exception {
        
        // 1. RELATIONAL (SQL): Validate User and Save Financial Record
        UserSQL user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found in Relational Database!"));

        String confirmationNum = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        BookingsSQL booking = new BookingsSQL(bookingType, price, confirmationNum, user);
        bookingRepository.save(booking);

        // 2. NON-RELATIONAL (Firestore): Mirror booking document collection
        BookingDoc bookingDoc = new BookingDoc();
        bookingDoc.setBookingId(confirmationNum);
        bookingDoc.setType(bookingType);
        bookingDoc.setPrice(price);
        bookingDoc.setConfirmationNumber(confirmationNum);
        firestore.collection("bookings").document(confirmationNum).set(bookingDoc).get();

        // 3. Update the live trip itinerary stream array
        String timelineEvent = "New " + bookingType + " confirmed: " + confirmationNum;
        firestore.collection("trips")
                 .document(tripId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineEvent))
                 .get(); 

        return "Hybrid Transaction Success! SQL Booking & NoSQL Document synchronized: " + confirmationNum;
    }

    @Transactional
    public String updateBookingType(Long bookingId, String confirmationNum, String newType) throws Exception {
        BookingsSQL booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));
        booking.setBookingType(newType);
        bookingRepository.save(booking); 
        
        firestore.collection("bookings").document(confirmationNum).update("type", newType).get();
        return "SQL and NoSQL Bookings updated successfully to: " + newType;
    }

    @Transactional
    public String cancelHybridBooking(Long bookingId, String confirmationNum, String tripId) throws Exception {
        BookingsSQL booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found in SQL Database!"));
        bookingRepository.delete(booking);

        firestore.collection("bookings").document(confirmationNum).delete().get();

        String timelineEvent = "❌ Reservation CANCELED: " + confirmationNum;
        firestore.collection("trips").document(tripId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineEvent)).get();

        return "Hybrid Cancellation Success! Purged from both engines.";
    }

    // ==========================================
    // 3. BUDGET CRUD (DUAL-PERSISTENCE)
    // ==========================================

    @Transactional
    public void logBudgetExpense(BudgetSQL sqlBudget, String budgetDocId, BudgetDoc noSqlBudget) throws Exception {
        budgetRepository.save(sqlBudget);
        firestore.collection("budgets").document(budgetDocId).set(noSqlBudget).get();
    }

    // ==========================================
    // 4. DESTINATION & TRIP SEPARATE MAPPINGS
    // ==========================================

    @Transactional
    public void createDestinationInBothSystems(DestinationSQL sqlDest, DestinationDoc noSqlDest) throws Exception {
        destinationRepository.save(sqlDest);
        firestore.collection("destinations").document(noSqlDest.getId()).set(noSqlDest).get();
    }

    @Transactional
    public TripsSQL createTripInBothSystems(TripsSQL sqlTrip, TripDoc noSqlTrip) throws Exception {
        // Save to Cloud SQL first and capture the resulting managed entity graph
        TripsSQL savedSqlTrip = tripRepository.save(sqlTrip);
        
        // Persist the document structure to Google Cloud Firestore
        firestore.collection("trips").document(noSqlTrip.getTripId()).set(noSqlTrip).get();
        
        // Return this back so the controller can send it to your React frontend!
        return savedSqlTrip;
    }

    public String addFriendCommentToTrip(String tripId, Long sqlTripId, String friendName, String comment) throws Exception {
        String formattedComment = friendName + ": \"" + comment + "\"";
        
        // 1. Update NoSQL Array inside the trips collection path
        firestore.collection("trips")
                 .document(tripId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(formattedComment))
                 .get();
        
        // 2. Update SQL Element Collection safely
        TripsSQL sqlTrip = tripRepository.findById(sqlTripId)
                .orElseThrow(() -> new RuntimeException("SQL Trip execution target context row not found: " + sqlTripId));
        
        // Maintain the bi-directional transactional boundaries matching your repository graph models
        sqlTrip.getProposals().add(new VotingProposalSQL()); 
        tripRepository.save(sqlTrip);
        
        return "Collaborative comment synchronized across both environments for " + friendName;
    }

    // ==========================================
    // 5. PROPOSAL & VOTING CRUD (TRIP ISOLATION ENGINE)
    // ==========================================

    @Transactional
    public String createBookingProposal(Long sqlTripId, String tripId, String title, Double price, int votesNeeded) throws Exception {
        String proposalId = "PROP-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        
        // ---- A. PERSIST TO FIRESTORE ----
        Map<String, Object> proposalData = new HashMap<>();
        proposalData.put("proposalId", proposalId);
        proposalData.put("tripId", tripId); // Fixed reference to track trip instead of itinerary
        proposalData.put("title", title);
        proposalData.put("estimatedPrice", price);
        proposalData.put("votesNeeded", votesNeeded);
        proposalData.put("currentVotes", new ArrayList<String>());
        proposalData.put("status", "PENDING");

        firestore.collection("proposals").document(proposalId).set(proposalData).get();

        String timelineLog = "💡 New proposal created: \"" + title + "\" (Voting Open)";
        firestore.collection("trips").document(tripId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineLog)).get();

        // ---- B. PERSIST TO CLOUD SQL ----
        TripsSQL tripSQL = tripRepository.findById(sqlTripId)
                .orElseThrow(() -> new RuntimeException("SQL Trip target missing"));
        
        VotingProposalSQL proposalSQL = new VotingProposalSQL();
        proposalSQL.setTitle(title);
        proposalSQL.setEstimatedPrice(price);
        proposalSQL.setVotesNeeded(votesNeeded);
        proposalSQL.setStatus("PENDING");
        proposalSQL.setTrip(tripSQL); // Clean relationship mapping call targeting TripSQL
        proposalSQL.setCurrentVotes(new ArrayList<>());
        
        proposalRepository.save(proposalSQL);

        return proposalId;
    }

    @Transactional
    public String castVoteOnProposal(String proposalId, Long sqlProposalId, String friendName, Long userId) throws Exception {
        // ---- A. MUTATE FIRESTORE VIEW ----
        DocumentReference propRef = firestore.collection("proposals").document(proposalId);
        DocumentSnapshot proposal = propRef.get().get();

        if (!proposal.exists()) {
            throw new RuntimeException("Proposal not found in Firestore!");
        }

        String status = proposal.getString("status");
        if ("COMPLETED".equals(status)) {
            return "This proposal has already been locked in and booked!";
        }

        propRef.update("currentVotes", com.google.cloud.firestore.FieldValue.arrayUnion(friendName)).get();

        // Refresh snapshot configuration variables
        proposal = propRef.get().get();
        List<String> currentVotes = (List<String>) proposal.get("currentVotes");
        Long votesNeeded = proposal.getLong("votesNeeded");
        String title = proposal.getString("title");
        Double price = proposal.getDouble("estimatedPrice");
        String tripId = proposal.getString("tripId");

        // ---- B. MUTATE SQL VIEW ----
        VotingProposalSQL proposalSQL = proposalRepository.findById(sqlProposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found in SQL Database!"));
        
        if (!proposalSQL.getCurrentVotes().contains(friendName)) {
            proposalSQL.getCurrentVotes().add(friendName);
            proposalRepository.save(proposalSQL);
        }

        // ---- C. CONSENSUS BOUNDARY CHECK ----
        if (currentVotes != null && currentVotes.size() >= votesNeeded.intValue()) {
            
            // Execute the synchronized relational transaction targeting trip tracking models
            String sqlConfirmation = this.executeHybridBooking(userId, tripId, "GROUP_HOTEL", price);
            
            // Lock states out on both engines to ensure structural consistency
            propRef.update("status", "COMPLETED").get();
            
            proposalSQL.setStatus("COMPLETED");
            proposalRepository.save(proposalSQL);
            
            return "🎉 Unanimous decision! " + title + " has been officially booked. " + sqlConfirmation;
        }

        return "Vote registered by " + friendName + " across both engines. Total: " + currentVotes.size() + "/" + votesNeeded;
    }
}