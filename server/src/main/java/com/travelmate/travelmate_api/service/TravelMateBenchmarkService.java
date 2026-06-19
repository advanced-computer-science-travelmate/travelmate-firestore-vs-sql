package com.travelmate.travelmate_api.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.dto.BenchmarkDTO;
import com.travelmate.travelmate_api.dto.TripRequestDTO;
import com.travelmate.travelmate_api.models.sql.BookingsSQL;
import com.travelmate.travelmate_api.models.sql.TripsSQL;
import com.travelmate.travelmate_api.models.sql.UserSQL;
import com.travelmate.travelmate_api.models.sql.VotingProposalSQL;
import com.travelmate.travelmate_api.repository.sql.BookingSQLRepository;
import com.travelmate.travelmate_api.repository.sql.TripSQLRepository;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;
import com.travelmate.travelmate_api.repository.sql.VotingProposalSQLRepository;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

public class TravelMateBenchmarkService {
	private final TripSQLRepository tripRepository;
    private final BookingSQLRepository bookingRepository;
    private final VotingProposalSQLRepository votingProposalRepository;
    private final UserSQLRepository userRepository;

    public TravelMateBenchmarkService(TripSQLRepository tripRepository, 
                            BookingSQLRepository bookingRepository, 
                            VotingProposalSQLRepository votingProposalRepository,
                            UserSQLRepository userRepository) {
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        this.votingProposalRepository = votingProposalRepository;
        this.userRepository = userRepository;
    }

    public BenchmarkDTO executeDynamicCloudBenchmark(TripRequestDTO request) throws Exception {
        // Prepare shared dynamic identifiers
        String tripIdStr = "TRIP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Long sqlTripId = System.currentTimeMillis(); // Unique relational ID

        // ==========================================
        // 🐬 ENGINE 1: GOOGLE CLOUD SQL (RELATIONAL)
        // ==========================================
        long sqlStartTime = System.currentTimeMillis();

        TripsSQL tripSQL = new TripsSQL();
        tripSQL.setId(sqlTripId);
        tripSQL.setStartDate(LocalDate.parse(request.getStartDate()));
        tripSQL.setEndDate(LocalDate.parse(request.getEndDate()));
        tripSQL.setMaxTravelers(request.getMaxTravelers());
        tripSQL.setAdults(request.getAdults());
        tripSQL.setChildren(request.getChildren());
        tripSQL.setRooms(request.getRooms());
        tripSQL.setSelectedPlaces(request.getDestinationName());
        tripRepository.save(tripSQL);

        Long userIdKey = Long.parseLong(request.getSqlUserId());
        UserSQL setExistingUser = userRepository.findById(userIdKey)
        	    .orElseThrow(() -> new RuntimeException("Parent User profile entity not found for ID: " + userIdKey));
        
        BookingsSQL bookingSQL = new BookingsSQL();
        bookingSQL.setId(sqlTripId);
        bookingSQL.setUser(setExistingUser);
        bookingSQL.setBookingType(request.getBookingType());
        bookingSQL.setConfirmationNumber(request.getConfirmationNumber());
        bookingSQL.setPrice(request.getPrice());
        bookingRepository.save(bookingSQL);

        VotingProposalSQL proposalSQL = new VotingProposalSQL();
        proposalSQL.setId(sqlTripId);
        proposalSQL.setTitle(request.getProposalTitle());
        proposalSQL.setStatus(request.getProposalStatus());
        proposalSQL.setVotesNeeded(request.getVotesNeeded());
        proposalSQL.setEstimatedPrice(request.getEstimatedPrice());
        votingProposalRepository.save(proposalSQL);

        long sqlEndTime = System.currentTimeMillis();
        long sqlDuration = sqlEndTime - sqlStartTime;

        // ==========================================
        // 🔥 ENGINE 2: GOOGLE CLOUD FIRESTORE (NOSQL)
        // ==========================================
        long noSqlStartTime = System.currentTimeMillis();

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("trips").document(tripIdStr);

        Map<String, Object> firestoreDocument = new HashMap<>();
        firestoreDocument.put("tripId", tripIdStr);
        firestoreDocument.put("sqlUserId", request.getSqlUserId());
        firestoreDocument.put("destinationName", request.getDestinationName());
        firestoreDocument.put("maxTravelers", request.getMaxTravelers());
        firestoreDocument.put("adults", request.getAdults());
        firestoreDocument.put("children", request.getChildren());
        firestoreDocument.put("rooms", request.getRooms());
        firestoreDocument.put("startDate", request.getStartDate());
        firestoreDocument.put("endDate", request.getEndDate());

        // Dynamic Embedded Bookings Array List
        List<Map<String, Object>> bookingsList = new ArrayList<>();
        Map<String, Object> embedBooking = new HashMap<>();
        embedBooking.put("bookingType", request.getBookingType());
        embedBooking.put("confirmationNumber", request.getConfirmationNumber());
        embedBooking.put("price", request.getPrice());
        bookingsList.add(embedBooking);
        firestoreDocument.put("embeddedBookings", bookingsList);

        // Dynamic Embedded Voting Proposals Array List
        List<Map<String, Object>> proposalsList = new ArrayList<>();
        Map<String, Object> embedProposal = new HashMap<>();
        embedProposal.put("title", request.getProposalTitle());
        embedProposal.put("status", request.getProposalStatus());
        embedProposal.put("votesNeeded", request.getVotesNeeded());
        embedProposal.put("estimatedPrice", request.getEstimatedPrice());
        embedProposal.put("voterNames", request.getVoterNames()); // Ingest dynamic text name array
        proposalsList.add(embedProposal);
        firestoreDocument.put("embeddedVotingProposals", proposalsList);

        // Sync save execution to compute true physical write delay
        ApiFuture<com.google.cloud.firestore.WriteResult> resultFuture = docRef.set(firestoreDocument);
        resultFuture.get(); 

        long noSqlEndTime = System.currentTimeMillis();
        long noSqlDuration = noSqlEndTime - noSqlStartTime;

        // Return metrics cleanly to display on your UI dashboard panels
        return new BenchmarkDTO(sqlDuration, noSqlDuration, tripIdStr);
    }
}
