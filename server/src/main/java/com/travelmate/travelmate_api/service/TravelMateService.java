package com.travelmate.travelmate_api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;
import com.travelmate.travelmate_api.models.nosql.UserDoc;
import com.travelmate.travelmate_api.models.nosql.BookingDoc;
import com.travelmate.travelmate_api.models.nosql.BudgetDoc;
import com.travelmate.travelmate_api.models.nosql.ItineraryDoc;
import com.travelmate.travelmate_api.models.nosql.ProposalDoc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;

// Import your SQL Models & Repositories (Matching your exact uppercase SQL convention)
import com.travelmate.travelmate_api.models.sql.UserSQL;
import com.travelmate.travelmate_api.models.sql.BookingsSQL;
import com.travelmate.travelmate_api.models.sql.BudgetSQL;
import com.travelmate.travelmate_api.models.sql.ItinerarySQL;
import com.travelmate.travelmate_api.models.sql.VotingProposalSQL;

import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;
import com.travelmate.travelmate_api.repository.sql.BookingSQLRepository;
import com.travelmate.travelmate_api.repository.sql.BudgetSQLRepository;
import com.travelmate.travelmate_api.repository.sql.ItinerarySQLRepository;
import com.travelmate.travelmate_api.repository.sql.VotingProposalSQLRepository;

// Import NoSQL DTOs
//import com.travelmate.travelmate_api.models.firestore.UserDoc;
//import com.travelmate.travelmate_api.models.firestore.BookingDoc;
//import com.travelmate.travelmate_api.models.firestore.BudgetDoc;
//import com.travelmate.travelmate_api.models.firestore.ItineraryDoc;
//import com.travelmate.travelmate_api.models.firestore.ProposalDoc;

@Service
public class TravelMateService {

    private final UserSQLRepository userRepository;
    private final BookingSQLRepository bookingRepository;
    private final BudgetSQLRepository budgetRepository;
    private final ItinerarySQLRepository itineraryRepository;
    private final VotingProposalSQLRepository proposalRepository;
    private final Firestore firestore;

    public TravelMateService(UserSQLRepository userRepository, 
                             BookingSQLRepository bookingRepository,
                             BudgetSQLRepository budgetRepository,
                             ItinerarySQLRepository itineraryRepository,
                             VotingProposalSQLRepository proposalRepository,
                             Firestore firestore) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.budgetRepository = budgetRepository;
        this.itineraryRepository = itineraryRepository;
        this.proposalRepository = proposalRepository;
        this.firestore = firestore;
    }

    // ==========================================
    // 1. USER CRUD (DUAL-PERSISTENCE)
    // ==========================================
    
    @Transactional
    public void createUserInBothSystems(UserSQL sqlUser, UserDoc noSqlUser) throws Exception {
        // SQL Save
        userRepository.save(sqlUser);
        // NoSQL Save
        firestore.collection("users").document(noSqlUser.getUserId()).set(noSqlUser).get();
    }

    @Transactional
    public void deleteUserFromBothSystems(Long sqlId, String noSqlId) throws Exception {
        userRepository.deleteById(sqlId);
        firestore.collection("users").document(noSqlId).delete().get();
    }

    // ==========================================
    // 2. BOOKING CRUD (YOUR EXPANDED CORE WORKFLOW)
    // ==========================================

    public List<BookingsSQL> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional 
    public String executeHybridBooking(Long userId, String itineraryId, String bookingType, Double price) throws Exception {
        
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

        // 3. Update the live timeline stream array
        String timelineEvent = "New " + bookingType + " confirmed: " + confirmationNum;
        firestore.collection("itineraries")
                 .document(itineraryId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineEvent))
                 .get(); 

        return "Hybrid Transaction Success! SQL Booking & NoSQL Document synchronized: " + confirmationNum;
    }

    @Transactional
    public String updateBookingType(Long bookingId, String confirmationNum, String newType) throws Exception {
        // Update SQL
        BookingsSQL booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));
        booking.setBookingType(newType);	// Booking Type
        bookingRepository.save(booking); 
        
        // Update Firestore Document
        firestore.collection("bookings").document(confirmationNum).update("type", newType).get();
        
        return "SQL and NoSQL Bookings updated successfully to: " + newType;
    }

    @Transactional
    public String cancelHybridBooking(Long bookingId, String confirmationNum, String itineraryId) throws Exception {
        // SQL Delete
        BookingsSQL booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found in SQL Database!"));
        bookingRepository.delete(booking);

        // Firestore Document Delete
        firestore.collection("bookings").document(confirmationNum).delete().get();

        // Append notice to live timeline
        String timelineEvent = "❌ Reservation CANCELED: " + confirmationNum;
        firestore.collection("itineraries").document(itineraryId)
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
    // 4. ITINERARY CRUD (DUAL-PERSISTENCE)
    // ==========================================

    @Transactional
    public void createItineraryInBothSystems(ItinerarySQL sqlItin, ItineraryDoc noSqlItin) throws Exception {
        itineraryRepository.save(sqlItin);
        firestore.collection("itineraries").document(noSqlItin.getItineraryId()).set(noSqlItin).get();
    }

    public String addFriendCommentToItinerary(String itineraryId, Long sqlItineraryId, String friendName, String comment) throws Exception {
        String formattedComment = friendName + ": \"" + comment + "\"";
        
        // 1. Update NoSQL Array
        firestore.collection("itineraries")
                 .document(itineraryId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(formattedComment))
                 .get();
        
        // 2. Update SQL Element Collection
        ItinerarySQL sqlItin = itineraryRepository.findById(sqlItineraryId)
                .orElseThrow(() -> new RuntimeException("SQL Itinerary not found"));
        sqlItin.getActivities().add(formattedComment);
        itineraryRepository.save(sqlItin);
        
        return "Collaborative comment synchronized across both environments for " + friendName;
    }

    // ==========================================
    // 5. PROPOSAL & VOTING CRUD (THE MASTER MIRROR)
    // ==========================================

    @Transactional
    public String createBookingProposal(Long sqlItineraryId, String itineraryId, String title, Double price, int votesNeeded) throws Exception {
        String proposalId = "PROP-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        
        // ---- A. PERSIST TO FIRESTORE ----
        Map<String, Object> proposalData = new HashMap<>();
        proposalData.put("proposalId", proposalId);
        proposalData.put("itineraryId", itineraryId);
        proposalData.put("title", title);
        proposalData.put("estimatedPrice", price);
        proposalData.put("votesNeeded", votesNeeded);
        proposalData.put("currentVotes", new ArrayList<String>());
        proposalData.put("status", "PENDING");

        firestore.collection("proposals").document(proposalId).set(proposalData).get();

        String timelineLog = "💡 New proposal created: \"" + title + "\" (Voting Open)";
        firestore.collection("itineraries").document(itineraryId)
                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineLog)).get();

        // ---- B. PERSIST TO CLOUD SQL ----
        ItinerarySQL itinerarySQL = itineraryRepository.findById(sqlItineraryId)
                .orElseThrow(() -> new RuntimeException("SQL Itinerary target missing"));
        
        VotingProposalSQL proposalSQL = new VotingProposalSQL();
        proposalSQL.setTitle(title);
        proposalSQL.setEstimatedPrice(price);
        proposalSQL.setVotesNeeded(votesNeeded);
        proposalSQL.setStatus("PENDING");
        proposalSQL.setItinerary(itinerarySQL);
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

        // Refresh snapshot
        proposal = propRef.get().get();
        List<String> currentVotes = (List<String>) proposal.get("currentVotes");
        Long votesNeeded = proposal.getLong("votesNeeded");
        String title = proposal.getString("title");
        Double price = proposal.getDouble("estimatedPrice");
        String itineraryId = proposal.getString("itineraryId");

        // ---- B. MUTATE SQL VIEW ----
        VotingProposalSQL proposalSQL = proposalRepository.findById(sqlProposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found in SQL Database!"));
        
        if (!proposalSQL.getCurrentVotes().contains(friendName)) {
            proposalSQL.getCurrentVotes().add(friendName);
            proposalRepository.save(proposalSQL);
        }

        // ---- C. CONSENSUS BOUNDARY CHECK ----
        if (currentVotes != null && currentVotes.size() >= votesNeeded.intValue()) {
            
            // Execute the synchronized relational transaction
            String sqlConfirmation = this.executeHybridBooking(userId, itineraryId, "GROUP_HOTEL", price);
            
            // Lock states out on both engines to ensure strict consistency
            propRef.update("status", "COMPLETED").get();
            
            proposalSQL.setStatus("COMPLETED");
            proposalRepository.save(proposalSQL);
            
            return "🎉 Unanimous decision! " + title + " has been officially booked. " + sqlConfirmation;
        }

        return "Vote registered by " + friendName + " across both engines. Total: " + currentVotes.size() + "/" + votesNeeded;
    }
}