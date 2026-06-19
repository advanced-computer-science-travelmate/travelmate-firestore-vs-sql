package com.travelmate.travelmate_api.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
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
    
    @PostMapping("/create")
    public ResponseEntity<?> createTrip(@RequestBody Map<String, Object> payload) throws Exception {
        
        String destinationName = (String) payload.get("destinationName");
        String startDateStr = (String) payload.get("startDate");
        String endDateStr = (String) payload.get("endDate");
        int maxTravelers = (Integer) payload.get("maxTravelers");
        
        String passedUserId = payload.containsKey("userId") ? (String) payload.get("userId") : "USER-ACTIVE-101";
        
        // 🚀 PROXIED API EXTERNAL ROUTING WITH RESILIENT HEADERS
        List<Map<String, Object>> apiResponse = null;
        try {
            // FIXED: Added fields parameter to retrieve country codes and flag assets properly
            String url = "https://restcountries.com/v3.1/region/europe?fields=name,flags,cca2";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            apiResponse = response.getBody();
        } catch (Exception e) {
            System.err.println("External REST Countries API unreachable. Processing local lifecycle rules: " + e.getMessage());
        }
        
        // 1. QUERY EXISTING MASTER CATALOG FOR DATA INTEGRITY
        DestinationSQL foundCatalogDest = destinationSqlRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(destinationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Destination not found in database: " + destinationName));
        
        TripsSQL sqlTrip = new TripsSQL();
        sqlTrip.setStartDate(LocalDate.parse(startDateStr));
        sqlTrip.setEndDate(LocalDate.parse(endDateStr));
        sqlTrip.setMaxTravelers(maxTravelers);
        
        sqlTrip.getDestination().add(foundCatalogDest);
        foundCatalogDest.setTrip(sqlTrip);
        
        // 2. CONSTRUCT NON-RELATIONAL DATA (Firestore Document)
        TripDoc noSqlTrip = new TripDoc();
        noSqlTrip.setTripId("TRIP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        noSqlTrip.setDestinationName(destinationName);
        noSqlTrip.setStartDate(startDateStr);
        noSqlTrip.setEndDate(endDateStr);
        noSqlTrip.setUserId(passedUserId); 

        // 3. PASS TO SYNCHRONIZED TRANSACTION AGENT PERSISTENCE
        // We do a single unified write via the service agent to maintain graph stability!
        TripsSQL savedSqlTrip = travelMateService.createTripInBothSystems(sqlTrip, noSqlTrip);
        
        // 🚀 BYPASS THE BROKEN WRAPPER: Return the real saved object containing your generated database table primary keys!
        return ResponseEntity.ok(savedSqlTrip);
    }

    // --- CLOUD SQL PATH ---
    @PostMapping("/sql")
    public ResponseEntity<TripsSQL> createTripSQL(@RequestBody TripsSQL trip) {
        return ResponseEntity.ok(tripSqlRepository.save(trip));
    }

    @GetMapping("/sql")
    public ResponseEntity<List<TripsSQL>> getAllTripsSQL() {
        return ResponseEntity.ok(tripSqlRepository.findAll());
    }

    // --- CLOUD FIRESTORE PATH ---
    @PostMapping("/firestore")
    public ResponseEntity<String> createTripNoSQL(@RequestBody TripDoc trip) throws Exception {
        firestore.collection("trips").document(trip.getTripId()).set(trip).get();
        return ResponseEntity.ok("Trip structured in Firestore");
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
    // 🗳️ GROUP VOTING INTEGRATION ENDPOINTS
    // ==========================================

    @GetMapping("/{tripId}/poll")
    public ResponseEntity<Map<String, Object>> getTripPoll(@PathVariable Long tripId) {
        TripsSQL trip = tripSqlRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip execution record context not found: " + tripId));

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
    public ResponseEntity<Map<String, Object>> createTripPoll(
            @PathVariable Long tripId, 
            @RequestBody Map<String, Object> payload) {
        
        TripsSQL trip = tripSqlRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip target context not found"));

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
    public ResponseEntity<Map<String, Object>> castVote(
            @PathVariable Long tripId,
            @PathVariable Long destinationId,
            @RequestParam String userId) {
        
        DestinationSQL destination = destinationSqlRepository.findById(destinationId)
            .orElseThrow(() -> new RuntimeException("Target voting option row context not found"));

        destination.setVotes(destination.getVotes() + 1);
        destinationSqlRepository.save(destination);

        return getTripPoll(tripId); 
    }
    
    // ==========================================
    // 🚀 DUAL-PERSISTENCE ITINERARY PLACES SYNC
    // ==========================================
    @PutMapping("/{tripId}/places")
    public ResponseEntity<?> updateTripPlaces(
            @PathVariable Long tripId, 
            @RequestBody Map<String, Object> fullSelectedMap) {
        
        Map<String, Object> metrics = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. GOOGLE CLOUD SQL OVERWRITEPersist (Using Correct tripSqlRepository Field)
        long sqlStart = System.currentTimeMillis();
        try {
            TripsSQL trip = tripSqlRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip profile not found inside SQL Instance"));
            
            String jsonString = objectMapper.writeValueAsString(fullSelectedMap);
            trip.setSelectedPlacesJson(jsonString);
            
            tripSqlRepository.save(trip); 
            
            metrics.put("cloudSqlWriteMs", (System.currentTimeMillis() - sqlStart));
            metrics.put("sqlStatus", "SUCCESS");
        } catch (Exception e) {
            metrics.put("sqlStatus", "FAILED");
            metrics.put("sqlError", e.getMessage());
        }

        // 2. GOOGLE CLOUD FIRESTORE UPDATE (Using Pre-injected Context Instance)
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
            metrics.put("firestoreError", e.getMessage());
        }

        return ResponseEntity.ok(metrics);
    }
}