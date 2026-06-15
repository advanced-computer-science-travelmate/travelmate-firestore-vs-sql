package com.travelmate.travelmate_api.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.models.nosql.BookingDoc;
import com.travelmate.travelmate_api.models.nosql.BudgetDoc;
import com.travelmate.travelmate_api.models.nosql.ItineraryDoc;
import com.travelmate.travelmate_api.models.nosql.ProposalDoc;
import com.travelmate.travelmate_api.models.nosql.UserDoc;
import com.travelmate.travelmate_api.models.sql.BookingsSQL;
import com.travelmate.travelmate_api.models.sql.BudgetSQL;
import com.travelmate.travelmate_api.models.sql.ItinerarySQL;
import com.travelmate.travelmate_api.models.sql.UserSQL;
import com.travelmate.travelmate_api.models.sql.VotingProposalSQL;
import com.travelmate.travelmate_api.repository.sql.BookingSQLRepository;
import com.travelmate.travelmate_api.repository.sql.BudgetSQLRepository;
import com.travelmate.travelmate_api.repository.sql.ItinerarySQLRepository;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;
import com.travelmate.travelmate_api.repository.sql.VotingProposalSQLRepository;

@Component
public class TravelMateConfigurationData implements CommandLineRunner {
	
    private final UserSQLRepository userRepository;
    private final ItinerarySQLRepository itineraryRepository;
    private final BookingSQLRepository bookingRepository;
    private final BudgetSQLRepository budgetRepository;
    private final VotingProposalSQLRepository proposalRepository;
    private final Firestore firestore;

    public TravelMateConfigurationData(UserSQLRepository userRepository,
                           ItinerarySQLRepository itineraryRepository,
                           BookingSQLRepository bookingRepository,
                           BudgetSQLRepository budgetRepository,
                           VotingProposalSQLRepository proposalRepository,
                           Firestore firestore) {
        this.userRepository = userRepository;
        this.itineraryRepository = itineraryRepository;
        this.bookingRepository = bookingRepository;
        this.budgetRepository = budgetRepository;
        this.proposalRepository = proposalRepository;
        this.firestore = firestore;
    }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("🧹 Flushing existing relational databases to clear duplicates...");
        
        // Clear all previous SQL records to prevent duplicate key constraint crashes
//        proposalRepository.deleteAll();
//        budgetRepository.deleteAll();
//        bookingRepository.deleteAll();
//        itineraryRepository.deleteAll();
//        userRepository.deleteAll();

        System.out.println("🚀 Executing Multi-Cloud Synchronized Data Seeding Engine...");

//        // ==========================================
//        // 1. SEED MODULE: USERS (Completely Randomized)
//        // ==========================================
//        UserSQL mainUserSql = new UserSQL("Arjun Mehta", "arjun.mehta@example.com");
//        mainUserSql = userRepository.save(mainUserSql);
//        System.out.println("✅ Seeded new user into Cloud SQL.");
//
//        UserDoc mainUserDoc = new UserDoc();
//        mainUserDoc.setUserId("USER-ARJUN-101");
//        mainUserDoc.setName("Arjun Mehta");
//        mainUserDoc.setEmail("arjun.mehta@example.com");
//        mainUserDoc.setEmbeddedBookings(new ArrayList<>());
//        
//        // Sync to Firestore
//        firestore.collection("users").document(mainUserDoc.getUserId()).set(mainUserDoc).get();
//
//
//        // ==========================================
//        // 2. SEED MODULE: ITINERARIES
//        // ==========================================
//        ItinerarySQL alpineSql = new ItinerarySQL();
//        alpineSql.setDestination("Chamonix Adventure");
//        alpineSql.setStartDate(LocalDate.of(2026, 7, 10));
//        alpineSql.setEndDate(LocalDate.of(2026, 7, 17));
//        alpineSql.setMaxTravelers(2);
//        alpineSql.setActivities(new ArrayList<>(List.of("🏔️ Aiguille du Midi Cable Car", "🥾 Mer de Glace Trekking")));
//        itineraryRepository.save(alpineSql);
//
//        ItineraryDoc alpineDoc = new ItineraryDoc();
//        alpineDoc.setItineraryId("ITIN-CHAMONIX-501");
//        alpineDoc.setDestination("Chamonix Adventure");
//        alpineDoc.setStartDate("2026-07-10");
//        alpineDoc.setEndDate("2026-07-17");
//        alpineDoc.setMaxTravelers(2);
//        alpineDoc.setActivities(new ArrayList<>(List.of("🏔️ Aiguille du Midi Cable Car", "🥾 Mer de Glace Trekking")));
//        
//        // Sync to Firestore
//        firestore.collection("itineraries").document(alpineDoc.getItineraryId()).set(alpineDoc).get();
//
//
//        // ==========================================
//        // 3. SEED MODULE: BOOKINGS & 4. BUDGETS
//        // ==========================================
//        // SQL Generation
//        BookingsSQL resortBookingSql = new BookingsSQL("HOTEL", 850.0, "CONF-CHAMONIXRESORT", mainUserSql);
//        bookingRepository.save(resortBookingSql);
//
//        BudgetSQL resortBudgetSql = new BudgetSQL("Accommodation", 850.0, resortBookingSql);
//        budgetRepository.save(resortBudgetSql);
//        
//        // Connect bi-directional backreferences for clean SQL mappings
//        resortBookingSql.setBudgetExpense(resortBudgetSql);
//        bookingRepository.save(resortBookingSql);
//
//        // Firestore Generation (Using structural embedding)
//        BudgetDoc resortBudgetDoc = new BudgetDoc();
//        resortBudgetDoc.setCategory("Accommodation");
//        resortBudgetDoc.setAmount(850.0);
//
//        BookingDoc resortBookingDoc = new BookingDoc();
//        resortBookingDoc.setBookingId("BOOK-CHAMONIX-501");
//        resortBookingDoc.setType("HOTEL");
//        resortBookingDoc.setPrice(850.0);
//        resortBookingDoc.setConfirmationNumber("CONF-CHAMONIXRESORT");
//        resortBookingDoc.setBudgetExpense(resortBudgetDoc);
//
//        // Save as individual benchmark records and update user collection arrays
//        firestore.collection("bookings").document(resortBookingDoc.getBookingId()).set(resortBookingDoc).get();
//        mainUserDoc.getEmbeddedBookings().add(resortBookingDoc);
//        firestore.collection("users").document(mainUserDoc.getUserId()).set(mainUserDoc).get();
//
//
//        // ==========================================
//        // 5. SEED MODULE: PROPOSALS & VOTING
//        // ==========================================
//        // SQL Generation
//        VotingProposalSQL proposalSQL = new VotingProposalSQL();
//        proposalSQL.setTitle("Stay at Glacier View Chalet");
//        proposalSQL.setEstimatedPrice(920.0);
//        proposalSQL.setVotesNeeded(2);
//        proposalSQL.setStatus("PENDING");
//        proposalSQL.setItinerary(alpineSql);
//        proposalSQL.setCurrentVotes(new ArrayList<>(List.of("Elena Fischer"))); // Friend group voter profile
//        proposalRepository.save(proposalSQL);
//
//        // Firestore Generation
//        ProposalDoc proposalDoc = new ProposalDoc();
//        proposalDoc.setProposalId("PROP-GLACIERCHALET");
//        proposalDoc.setItineraryId(alpineDoc.getItineraryId());
//        proposalDoc.setTitle("Stay at Glacier View Chalet");
//        proposalDoc.setEstimatedPrice(920.0);
//        proposalDoc.setVotesNeeded(2);
//        proposalDoc.setStatus("PENDING");
//        proposalDoc.setCurrentVotes(new ArrayList<>(List.of("Elena Fischer")));
//        
//        // Sync to Firestore
//        firestore.collection("proposals").document(proposalDoc.getProposalId()).set(proposalDoc).get();

        System.out.println("🎉 Multi-cloud datasets successfully populated into Cloud SQL and Firestore clusters.");
    }
}