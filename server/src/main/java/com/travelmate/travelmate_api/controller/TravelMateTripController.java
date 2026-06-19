package com.travelmate.travelmate_api.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.travelmate.travelmate_api.models.nosql.TripDoc;
import com.travelmate.travelmate_api.models.sql.DestinationSQL;
import com.travelmate.travelmate_api.models.sql.TripsSQL;
import com.travelmate.travelmate_api.repository.sql.DestinationSQLRepository;
import com.travelmate.travelmate_api.repository.sql.TripSQLRepository;
import com.travelmate.travelmate_api.service.TravelMateService;

@RestController
@RequestMapping("/api/travel/trips")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateTripController {

    private final TripSQLRepository tripSqlRepository;
    private final DestinationSQLRepository destinationSqlRepository;
    private final TravelMateService travelMateService;
    private final Firestore firestore;
    private final RestTemplate restTemplate = new RestTemplate();

    public TravelMateTripController(TripSQLRepository tripSqlRepository, 
                                    DestinationSQLRepository destinationSqlRepository, 
                                    TravelMateService travelMateService, 
                                    Firestore firestore) {
        this.tripSqlRepository = tripSqlRepository;
        this.destinationSqlRepository = destinationSqlRepository;
        this.travelMateService = travelMateService;
        this.firestore = firestore;
    }
    
    // 🟢 UNIFIED DUAL-WRITE CREATION (KEEP THIS - IT WRITES TO BOTH CLOUDS)
    @PostMapping("/create")
    public ResponseEntity<?> createTrip(@RequestBody Map<String, Object> payload) throws Exception {
        
        // Extract parameters directly from the dynamic network payload
        String destinationName = (String) payload.get("destinationName");
        String startDateStr = (String) payload.get("startDate");
        String endDateStr = (String) payload.get("endDate");
        
        // 1. Relational Integrity Query (Validates catalog state dynamically)
        DestinationSQL foundCatalogDest = destinationSqlRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(destinationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Target destination context missing from active catalog mapping."));
        
        // 2. Map Relational Column State
        TripsSQL sqlTrip = new TripsSQL();
        sqlTrip.setStartDate(LocalDate.parse(startDateStr));
        sqlTrip.setEndDate(LocalDate.parse(endDateStr));
        
        // Dynamically safely extract whatever additional numeric variables are passed from the client
        if (payload.containsKey("maxTravelers")) sqlTrip.setMaxTravelers((Integer) payload.get("maxTravelers"));
        if (payload.containsKey("adults")) sqlTrip.setAdults((Integer) payload.get("adults"));
        if (payload.containsKey("children")) sqlTrip.setChildren((Integer) payload.get("children"));
        if (payload.containsKey("rooms")) sqlTrip.setRooms((Integer) payload.get("rooms"));
        
        sqlTrip.getDestination().add(foundCatalogDest);
        foundCatalogDest.setTrip(sqlTrip);
        
        // 3. Build Dynamic Non-Relational Document Node
        TripDoc noSqlTrip = new TripDoc();
        
        String generatedId = "TRIP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        noSqlTrip.setTripId(generatedId);

        // Ensure your incoming payload map also carries this ID so it is stored in the document body
        payload.put("tripId", generatedId);
        
        // Safely forward the incoming map straight to your service provider layer
        // This allows your React application to completely dictate the embedded properties 
        // (bookings, budgets, timelines) without modifying backend compiler rules.
        TripsSQL savedSqlTrip = travelMateService.createTripInBothSystems(sqlTrip, noSqlTrip);
        
        // Update the live Firestore instance directly with the dynamic payload data map structure
        firestore.collection("trips")
                 .document(noSqlTrip.getTripId())
                 .set(payload) // 🚀 FORWARDS RAW FRONTEND JSON MAP ENTIRELY
                 .get();
        
        return ResponseEntity.ok(savedSqlTrip);
    }
    
    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seedBulkTrips(@RequestParam(defaultValue = "1000") int count, @RequestBody Map<String, Object> basePayload) {
        Map<String, Object> responseMetrics = new HashMap<>();
        java.util.Random random = new java.util.Random();
        
        long startTime = System.currentTimeMillis();
        int successCount = 0;

        // Pull the active dataset straight from your database to eliminate hardcoded values
        List<DestinationSQL> activeCatalog = destinationSqlRepository.findAll();
        if (activeCatalog.isEmpty()) {
            throw new IllegalStateException("Cannot run benchmarking seed operations: Master destination catalog table is empty.");
        }

        for (int i = 1; i <= count; i++) {
            try {
                // Randomly select an authentic destination row directly from the catalog
                DestinationSQL randomDest = activeCatalog.get(random.nextInt(activeCatalog.size()));
                
                TripsSQL sqlTrip = new TripsSQL();
                sqlTrip.setStartDate(LocalDate.now().plusDays(random.nextInt(30)));
                sqlTrip.setEndDate(sqlTrip.getStartDate().plusDays(5 + random.nextInt(10)));
                
                sqlTrip.getDestination().add(randomDest);
                randomDest.setTrip(sqlTrip);
                
                TripDoc noSqlTrip = new TripDoc();
                noSqlTrip.setTripId("TRIP-BULK-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                
                // Reconstruct the dynamic map payload to match the database state
                Map<String, Object> dynamicPayload = new HashMap<>(basePayload);
                dynamicPayload.put("tripId", noSqlTrip.getTripId());
                dynamicPayload.put("destinationName", randomDest.getName());
                dynamicPayload.put("startDate", sqlTrip.getStartDate().toString());
                dynamicPayload.put("endDate", sqlTrip.getEndDate().toString());
                
                travelMateService.createTripInBothSystems(sqlTrip, noSqlTrip);
                firestore.collection("trips").document(noSqlTrip.getTripId()).set(dynamicPayload);
                
                successCount++;
            } catch (Exception e) {
                System.err.println("Skipping row insertion: " + e.getMessage());
            }
        }

        responseMetrics.put("successfullySynced", successCount);
        responseMetrics.put("executionDurationMs", System.currentTimeMillis() - startTime);
        
        return ResponseEntity.ok(responseMetrics);
    }

    // --- BENCHMARKING READ TARGETS ---
    @GetMapping("/sql")
    public ResponseEntity<List<TripsSQL>> getAllTripsSQL() {
        return ResponseEntity.ok(tripSqlRepository.findAll());
    }

    @GetMapping("/firestore")
    public ResponseEntity<List<TripDoc>> getAllTripsNoSQL() throws Exception {
        var future = firestore.collection("trips").get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<TripDoc> list = new ArrayList<>();
        for (var doc : docs) {
            list.add(doc.toObject(TripDoc.class));
        }
        return ResponseEntity.ok(list);
    }
    
    // ==========================================
    // 🗳️ RELATIONAL GROUP VOTING ENDPOINTS
    // ==========================================
    @GetMapping("/{tripId}/poll")
    public ResponseEntity<Map<String, Object>> getTripPoll(@PathVariable Long tripId) {
        TripsSQL trip = tripSqlRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip record not found: " + tripId));

        Map<String, Object> response = new HashMap<>();
        response.put("id", trip.getId());
        response.put("title", "Vote for " + (trip.getDestination().isEmpty() ? "Destinations" : trip.getDestination().get(0).getName()));
        
        List<Map<String, Object>> options = trip.getDestination().stream().map(d -> {
            Map<String, Object> opt = new HashMap<>();
            opt.put("id", d.getId());
            opt.put("destination", d.getName());
            opt.put("votes", d.getVotes());
            return opt;
        }).collect(Collectors.toList());

        response.put("options", options);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tripId}/poll/create")
    public ResponseEntity<Map<String, Object>> createTripPoll(@PathVariable Long tripId, @RequestBody Map<String, Object> payload) {
        TripsSQL trip = tripSqlRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip context not found"));
        List<Map<String, Object>> optionsList = (List<Map<String, Object>>) payload.get("options");

        for (Map<String, Object> opt : optionsList) {
            String name = (String) opt.get("name");
            DestinationSQL pollOption = new DestinationSQL();
            pollOption.setName(name);
            pollOption.setVotes(0);
            pollOption.setTrip(trip); 
            destinationSqlRepository.save(pollOption);
        }
        return getTripPoll(tripId);
    }

    @PostMapping("/{tripId}/destinations/{destinationId}/vote")
    public ResponseEntity<Map<String, Object>> castVote(@PathVariable Long tripId, @PathVariable Long destinationId, @RequestParam String userId) {
        DestinationSQL destination = destinationSqlRepository.findById(destinationId).orElseThrow(() -> new RuntimeException("Target row not found"));
        destination.setVotes(destination.getVotes() + 1);
        destinationSqlRepository.save(destination);
        return getTripPoll(tripId); 
    }
    
    // ==========================================
    // 🚀 DUAL-PERSISTENCE ITINERARY PLACES SYNC
    // ==========================================
    @PutMapping("/{tripId}/places")
    public ResponseEntity<?> updateTripPlaces(@PathVariable Long tripId, @RequestBody Map<String, Object> fullSelectedMap) {
        Map<String, Object> metrics = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. Write to Cloud SQL
        long sqlStart = System.currentTimeMillis();
        try {
            TripsSQL trip = tripSqlRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found inside SQL"));
            trip.setSelectedPlacesJson(objectMapper.writeValueAsString(fullSelectedMap));
            tripSqlRepository.save(trip); 
            metrics.put("cloudSqlWriteMs", (System.currentTimeMillis() - sqlStart));
            metrics.put("sqlStatus", "SUCCESS");
        } catch (Exception e) {
            metrics.put("sqlStatus", "FAILED");
        }

        // 2. Write to Cloud Firestore
        long firestoreStart = System.currentTimeMillis();
        try {
            ApiFuture<WriteResult> future = this.firestore.collection("trips")
                    .document(String.valueOf(tripId))
                    .update("selectedPlacesByDay", fullSelectedMap);
            future.get();
            metrics.put("firestoreWriteMs", (System.currentTimeMillis() - firestoreStart));
            metrics.put("firestoreStatus", "SUCCESS");
        } catch (Exception e) {
            metrics.put("firestoreStatus", "FAILED");
        }

        return ResponseEntity.ok(metrics);
    }
}