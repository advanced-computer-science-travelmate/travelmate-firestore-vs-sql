package com.travelmate.travelmate_api.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.models.sql.UserSQL;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;

@Component
public class TravelMateConfigurationData implements CommandLineRunner {
	
	private final UserSQLRepository userRepository;
    private final Firestore firestore;

    public TravelMateConfigurationData(UserSQLRepository userRepository, Firestore firestore) {
        this.userRepository = userRepository;
        this.firestore = firestore;
    }
    
    

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if (userRepository.count() > 1) {
            System.out.println("⚠️ Dummy data already exists. Skipping initialization.");
            return;
        }

        System.out.println("🚀 Starting Master Multi-Cloud Data Initialization...");

        // ==========================================
        // 1. GENERATE 10 USERS (MySQL)
        // ==========================================
        List<UserSQL> users = new ArrayList<>();
        String[] names = {"Siddharth", "John", "Emma", "Niklas", "Sarah", "David", "Anna", "Michael", "Laura", "Daniel"};
        
        for (int i = 0; i < names.length; i++) {
            UserSQL u = new UserSQL(names[i], names[i].toLowerCase() + "@example.com");
            users.add(userRepository.save(u));
        }
        System.out.println("✅ 10 Users successfully inserted into MySQL.");

        // ==========================================
        // 2. GENERATE 10 ITINERARIES (Firestore)
        // ==========================================
        String[] itineraryIds = {
            "itin-heidelberg-2026", "itin-berlin-getaway", "itin-munich-fest", 
            "itin-blackforest-hike", "itin-hamburg-port", "itin-frankfurt-city", 
            "itin-cologne-cathedral", "itin-stuttgart-tech", "itin-dresden-art", "itin-castle-tour"
        };
        
        String[] destinations = {"Heidelberg", "Berlin", "Munich", "Black Forest", "Hamburg", "Frankfurt", "Cologne", "Stuttgart", "Dresden", "Füssen"};

        for (int i = 0; i < itineraryIds.length; i++) {
            Map<String, Object> itinData = new HashMap<>();
            itinData.put("itineraryId", itineraryIds[i]);
            itinData.put("destination", destinations[i]);
            
            List<String> activities = new ArrayList<>();
            activities.add("📅 Itinerary created for group trip to " + destinations[i]);
            activities.add("👥 Initial planning group synchronized.");
            itinData.put("activities", activities);

            firestore.collection("itineraries").document(itineraryIds[i]).set(itinData).get();
        }
        System.out.println("✅ 10 Core Itineraries successfully initialized in Firestore.");

        // ==========================================
        // 3. GENERATE 40 PROPOSALS (Firestore)
        // ==========================================
        String[] itemTypes = {"Hotel", "AirBnB", "Train Ticket", "Guided Tour", "Rental Car", "Flight Ticket", "Museum Pass", "Group Dinner"};
        Double[] priceBaselines = {120.00, 250.50, 49.00, 35.00, 89.90, 180.00, 25.00, 65.00};

        for (int i = 1; i <= 40; i++) {
            String proposalId = "PROP-" + (1000 + i);
            String targetItineraryId = itineraryIds[i % 10]; // Distribute evenly across the 10 itineraries
            String type = itemTypes[i % itemTypes.length];
            Double price = priceBaselines[i % priceBaselines.length] + (i * 2.5); // Add variance to price
            
            Map<String, Object> propData = new HashMap<>();
            propData.put("proposalId", proposalId);
            propData.put("itineraryId", targetItineraryId);
            propData.put("title", "Proposed " + type + " (Option #" + i + ")");
            propData.put("estimatedPrice", Math.round(price * 100.0) / 100.0);
            
            // Alternating requirements and voting status to look realistic
            int votesNeeded = (i % 2 == 0) ? 3 : 4;
            propData.put("votesNeeded", votesNeeded);
            
            List<String> voters = new ArrayList<>();
            String status = "PENDING";
            
            if (i % 4 == 0) {
                // Simulate a fully voted proposal
                voters.add("Siddharth");
                voters.add("John");
                voters.add("Emma");
                if (votesNeeded == 4) voters.add("Niklas");
                status = "COMPLETED";
            } else if (i % 3 == 0) {
                // Simulate a partially voted proposal
                voters.add("Siddharth");
                voters.add("Sarah");
            }

            propData.put("currentVotes", voters);
            propData.put("status", status);

            firestore.collection("proposals").document(proposalId).set(propData).get();
            
            // Append a log to the respective itinerary timeline for context
            String logMessage = "💡 Suggestion added: Proposed " + type + " (#" + i + ") - €" + propData.get("estimatedPrice");
            firestore.collection("itineraries").document(targetItineraryId)
                     .update("activities", com.google.cloud.firestore.FieldValue.arrayUnion(logMessage)).get();
        }

        System.out.println("✅ 40 Multi-State Proposals successfully generated in Firestore.");
        System.out.println("🎉 Hybrid Cloud Data Mocking Sequence Complete!");
	}

}
