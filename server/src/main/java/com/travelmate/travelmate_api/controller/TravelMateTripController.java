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
import org.springframework.http.HttpStatus;
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
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
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
    public ResponseEntity<?> createTrip(@RequestBody Map<String, Object> payload) {
        String destinationName = (String) payload.get("destinationName");
        String startDateStr = (String) payload.get("startDate");
        String endDateStr = (String) payload.get("endDate");
        String passedUserId = (String) payload.get("userId");
        
        Integer maxTravelers = payload.get("maxTravelers") != null ? 
                Integer.parseInt(payload.get("maxTravelers").toString()) : 2;

        // Live API Handshake logic
        String countryFlag = "🏳️"; 
        String countryCode = "EU"; 
        
        final String finalCountryFlag = countryFlag;
        try {
            String url = "https://restcountries.com/v3.1/region/europe?fields=name,flags,cca2";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> apiResponse = response.getBody();
            
            if (apiResponse != null) {
                for (Map<String, Object> countryMap : apiResponse) {
                    Map<String, Object> nameObj = (Map<String, Object>) countryMap.get("name");
                    String commonName = (String) nameObj.get("common");
                    
                    if (commonName != null && commonName.equalsIgnoreCase(destinationName)) {
                        countryCode = (String) countryMap.get("cca2");
                        Map<String, Object> flagsObj = (Map<String, Object>) countryMap.get("flags");
                        countryFlag = flagsObj != null ? (String) flagsObj.get("png") : "🏳️";
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("REST Countries proxy fallback triggered: " + e.getMessage());
        }

        // 1. RESOLVE OR SEED IN CLOUD SQL
        DestinationSQL foundCatalogDest = destinationSqlRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(destinationName))
                .findFirst()
                .orElseGet(() -> {
                    DestinationSQL newDest = new DestinationSQL();
                    newDest.setName(destinationName);
                    newDest.setImage(finalCountryFlag);
                    return destinationSqlRepository.save(newDest);
                });

        TripsSQL sqlTrip = new TripsSQL();
        sqlTrip.setStartDate(LocalDate.parse(startDateStr));
        sqlTrip.setEndDate(LocalDate.parse(endDateStr));
        sqlTrip.setMaxTravelers(maxTravelers);
        
        sqlTrip.getDestination().add(foundCatalogDest);
        foundCatalogDest.setTrip(sqlTrip);

        String generatedTripId = "TRIP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. CONSTRUCT THE EXACT TRIPDOC INSTANCE EXPECTED BY THE SERVICE PARAMETER
        TripDoc noSqlTrip = new TripDoc();
        noSqlTrip.setTripId(generatedTripId);
        noSqlTrip.setDestinationName(destinationName);
        noSqlTrip.setStartDate(startDateStr);
        noSqlTrip.setEndDate(endDateStr);
        noSqlTrip.setUserId(passedUserId);

        // 3. BIND DETAILED ATTRIBUTES TO A MAP FOR FIRESTORE SAVING 
        Map<String, Object> firestorePayload = new HashMap<>(payload);
        firestorePayload.put("tripId", generatedTripId);
        firestorePayload.put("countryCode", countryCode);
        firestorePayload.put("countryFlag", countryFlag);

        try {
            // 🟢 MATCHES SIGNATURE PERFECTLY: Passing (TripsSQL, TripDoc) without touching the service file
            TripsSQL savedSqlTrip = travelMateService.createTripInBothSystems(sqlTrip, noSqlTrip);
            
            // Persist the complete map into Firestore directly
            firestore.collection("trips")
                     .document(generatedTripId)
                     .set(firestorePayload)
                     .get();
            
            return ResponseEntity.ok(savedSqlTrip);
            
        } catch (Exception e) {
            System.err.println("Multi-Cloud write transaction failure: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Sync failure", "details", e.getMessage()));
        }
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
    
    @GetMapping("/countries/list")
    public ResponseEntity<List<Map<String, Object>>> getEuropeanCountriesCatalog() {
        List<Map<String, Object>> countryCatalog = new ArrayList<>();
        
        try {
            // 🚀 SECURE LIVE STREAM: Using a high-availability, open-source world country repository
            String url = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String rawJson = response.getBody();
            
            if (rawJson != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(rawJson);
                
                if (rootNode.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode countryNode : rootNode) {
                        // 🟢 Filter dynamically by Europe region on the fly!
                        String region = countryNode.path("region").asText("");
                        
                        if ("Europe".equalsIgnoreCase(region)) {
                            com.fasterxml.jackson.databind.JsonNode nameNode = countryNode.path("name");
                            String commonName = nameNode.path("common").asText("");
                            String cca2 = countryNode.path("cca2").asText("").toLowerCase(); // 🔥 FORCE LOWERCASE HERE

                            if (!commonName.isEmpty() && !cca2.isEmpty()) {
                                Map<String, Object> compactCountry = new HashMap<>();
                                compactCountry.put("id", cca2.toUpperCase()); // Keep ID uppercase for standard display
                                compactCountry.put("name", commonName);
                                
                                // 🟢 This guarantees the URL is completely lowercase (e.g., /w320/dk.png)
                                compactCountry.put("image", "https://flagcdn.com/w320/" + cca2 + ".png");
                                
                                countryCatalog.add(compactCountry);
                            }
                        }
                    }
                }
                
                // Keep the sorting clean and alphabetical
                countryCatalog.sort((a, b) -> ((String) a.get("name")).compareTo((String) b.get("name")));
            }
        } catch (Exception e) {
            System.err.println("Live dynamic catalog sync exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(countryCatalog);
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTripFromBothClouds(@PathVariable Long id) {
        Map<String, Object> responseMetrics = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 1. Fetch matching SQL record using your existing repository instance
            TripsSQL targetTrip = tripSqlRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Target trip execution id not found in Cloud SQL: " + id));

            // 2. Clear from Cloud Firestore first using both common id patterns
            // Since your app maps records using both numeric IDs and string lookups, 
            // we will issue safe deletes for both to ensure Firestore is completely clean!
            try {
                // Delete document if it's named by the numeric SQL ID string (e.g., "71")
                this.firestore.collection("trips").document(String.valueOf(id)).delete().get();
                
                // If your application saves Firestore entries by their destination name, clear that too
                if (targetTrip.getDestination() != null && !targetTrip.getDestination().isEmpty()) {
                    String destinationName = targetTrip.getDestination().get(0).getName();
                    
                    // Optional: Query and delete by destinationName field if your IDs are dynamic UUIDs
                    this.firestore.collection("trips")
                        .whereEqualTo("destinationName", destinationName)
                        .get()
                        .get()
                        .getDocuments()
                        .forEach(doc -> doc.getReference().delete());
                }
                
                responseMetrics.put("firestoreDeleteStatus", "SUCCESS_OR_CLEANED");
            } catch (Exception fe) {
                System.err.println("Firestore node missing or unmapped, proceeding with SQL wipe: " + fe.getMessage());
                responseMetrics.put("firestoreDeleteStatus", "SKIPPED_OR_EMPTY");
            }

            // 3. Clear from Cloud SQL
            tripSqlRepository.deleteById(id);
            responseMetrics.put("sqlDeleteStatus", "SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            responseMetrics.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(responseMetrics);
        }

        responseMetrics.put("executionDurationMs", System.currentTimeMillis() - startTime);
        return ResponseEntity.ok(responseMetrics);
    }
}