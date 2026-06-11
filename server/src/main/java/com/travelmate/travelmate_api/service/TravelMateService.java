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
import com.travelmate.travelmate_api.models.sql.Bookings;
import com.travelmate.travelmate_api.models.sql.User;
import com.travelmate.travelmate_api.repository.sql.BookingRepository;
import com.travelmate.travelmate_api.repository.sql.UserRepository;

@Service
public class TravelMateService {

	    private final UserRepository userRepository;
	    private final BookingRepository bookingRepository;
	    private final Firestore firestore;

	    public TravelMateService(UserRepository userRepository, BookingRepository bookingRepository, Firestore firestore) {
	        this.userRepository = userRepository;
	        this.bookingRepository = bookingRepository;
	        this.firestore = firestore;
	    }
	    
	    public List<Bookings> getUserBookings(Long userId) {
	        return bookingRepository.findByUserId(userId);
	    }
	    
	    public String addFriendCommentToItinerary(String itineraryId, String friendName, String comment) throws Exception {
	        String formattedComment = friendName + ": \"" + comment + "\"";
	        
	        // Uses Firestore's native arrayUnion to safely append a new string 
	        // to the collaborative list, even if multiple friends hit it at once!
	        firestore.collection("itineraries")
	                 .document(itineraryId)
	                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(formattedComment))
	                 .get();
	        
	        return "Collaborative update synchronized! Comment added by " + friendName;
	    }
	    
	    @Transactional 
	    public String executeHybridBooking(Long userId, String itineraryId, String bookingType, Double price) throws Exception {
	        
	        // 1. RELATIONAL (SQL): Validate User and Save Financial Record
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found in Relational Database!"));

	        String confirmationNum = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	        Bookings booking = new Bookings(bookingType, price, confirmationNum, user);
	        bookingRepository.save(booking);

	        // 2. NON-RELATIONAL (Firestore): Update the live, dynamic timeline
	        String timelineEvent = "New " + bookingType + " confirmed: " + confirmationNum;
	        
	        // Appending to the NoSQL document array
	        firestore.collection("itineraries")
	                 .document(itineraryId)
	                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineEvent))
	                 .get(); // .get() forces the thread to wait for Google's cloud response

	        return "Hybrid Transaction Success! SQL Booking: " + confirmationNum + " | Firestore Itinerary Updated.";
	    }
	    
	    @Transactional
	    public String updateBookingType(Long bookingId, String newType) {
	        Bookings booking = bookingRepository.findById(bookingId)
	                .orElseThrow(() -> new RuntimeException("Booking not found!"));
	        
	        // Change the internal value
	        booking.setBookingType(newType);
	        
	        // Save triggers a clean SQL UPDATE statement behind the scenes
	        bookingRepository.save(booking); 
	        
	        return "SQL Booking updated successfully to: " + newType;
	    }
	    
	    @Transactional
	    public String cancelHybridBooking(Long bookingId, String itineraryId) throws Exception {
	        
	        // 1. Fetch the exact booking to get its details before we delete it
	        Bookings booking = bookingRepository.findById(bookingId)
	                .orElseThrow(() -> new RuntimeException("Booking not found in SQL Database!"));

	        String confirmationNum = booking.getConfirmationNumber();

	        // 2. SQL: Hard delete the record (In a real production app, you might just update a 'status' column to CANCELLED instead)
	        bookingRepository.delete(booking);

	        // 3. Firestore: Append a cancellation notice to the live timeline array
	        String timelineEvent = "❌ Reservation CANCELED: " + confirmationNum;
	        
	        firestore.collection("itineraries")
	                 .document(itineraryId)
	                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineEvent))
	                 .get();

	        return "Hybrid Cancellation Success! SQL Booking (" + confirmationNum + ") removed | Firestore Itinerary Updated.";
	    }
	    
	    public String createBookingProposal(String itineraryId, String title, Double price, int votesNeeded) throws Exception {
	        String proposalId = "PROP-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
	        
	        Map<String, Object> proposalData = new HashMap<>();
	        proposalData.clear();
	        proposalData.put("proposalId", proposalId);
	        proposalData.put("itineraryId", itineraryId);
	        proposalData.put("title", title);
	        proposalData.put("estimatedPrice", price);
	        proposalData.put("votesNeeded", votesNeeded);
	        proposalData.put("currentVotes", new ArrayList<String>()); // Starts with empty list of voter names
	        proposalData.put("status", "PENDING");

	        firestore.collection("proposals").document(proposalId).set(proposalData).get();
	        
	        // Log this creation event onto the main itinerary timeline as well
	        String timelineLog = "💡 New proposal created: \"" + title + "\" (Voting Open)";
	        firestore.collection("itineraries").document(itineraryId)
	                 .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(timelineLog)).get();

	        return proposalId;
	    }

	    /**
	     * HYBRID HYBRID HOOK: Cast a vote in Firestore. If consensus is hit, lock it into MySQL!
	     */
	    @Transactional
	    public String castVoteOnProposal(String proposalId, String friendName, Long userId) throws Exception {
	        DocumentReference propRef = firestore.collection("proposals").document(proposalId);
	        DocumentSnapshot proposal = propRef.get().get();

	        if (!proposal.exists()) {
	            throw new RuntimeException("Proposal not found!");
	        }

	        String status = proposal.getString("status");
	        if ("COMPLETED".equals(status)) {
	            return "This proposal has already been locked in and booked!";
	        }

	        // 1. Add friend's name to the Firestore voting array securely
	        propRef.update("currentVotes", com.google.cloud.firestore.FieldValue.arrayUnion(friendName)).get();

	        // 2. Fetch fresh snapshot to check if we hit the limit
	        proposal = propRef.get().get();
	        List<String> currentVotes = (List<String>) proposal.get("currentVotes");
	        Long votesNeeded = proposal.getLong("votesNeeded");
	        String title = proposal.getString("title");
	        Double price = proposal.getDouble("estimatedPrice");
	        String itineraryId = proposal.getString("itineraryId");

	        // 3. Consensus Check! If the vote list matches the requirements, finalize to SQL
	        if (currentVotes != null && currentVotes.size() >= votesNeeded.intValue()) {
	            
	            // Trigger your existing SQL relational booking routine!
	            String sqlConfirmation = this.executeHybridBooking(userId, itineraryId, "GROUP_HOTEL", price);
	            
	            // Mark proposal as finalized so nobody can double-vote
	            propRef.update("status", "COMPLETED").get();
	            
	            return "🎉 Unanimous decision! " + title + " has been officially booked in MySQL ledger. " + sqlConfirmation;
	        }

	        return "Vote registered by " + friendName + ". Total votes: " + currentVotes.size() + "/" + votesNeeded;
	    }
}
