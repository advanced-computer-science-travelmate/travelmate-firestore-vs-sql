package com.travelmate.travelmate_api.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
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

    public TravelMateTripController(TripSQLRepository tripSqlRepository, DestinationSQLRepository destinationSqlRepository, 
    								TravelMateService travelMateService, Firestore firestore) {
        this.tripSqlRepository = tripSqlRepository;
        this.destinationSqlRepository = destinationSqlRepository;
        this.travelMateService = travelMateService;
        this.firestore = firestore;
    }
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @PostMapping("/create")
    public ResponseEntity<String> createTrip(@RequestBody Map<String, Object> payload) throws Exception {
        
        String destinationName = (String) payload.get("destinationName");
        String startDateStr = (String) payload.get("startDate");
        String endDateStr = (String) payload.get("endDate");
        int maxTravelers = (Integer) payload.get("maxTravelers");
        
        String passedUserId = payload.containsKey("userId") ? (String) payload.get("userId") : "USER-ACTIVE-101";
        String passedUserName = payload.containsKey("userName") ? (String) payload.get("userName") : "Siddharth D";
        
        String url = "https://restcountries.com/v3.1/region/europe?fields=name";
        List<Map<String, Object>> apiResponse = restTemplate.getForObject(url, List.class);
        
        // Extract the common English names of all European countries dynamically
        List<String> europeanCountryNames = new ArrayList<>();
        if (apiResponse != null) {
            for (Map<String, Object> countryData : apiResponse) {
                Map<String, Object> nameObj = (Map<String, Object>) countryData.get("name");
                if (nameObj != null && nameObj.containsKey("common")) {
                    europeanCountryNames.add((String) nameObj.get("common"));
                }
            }
        }
        
        // 1. QUERY EXISTING MASTER CATALOG FOR DATA INTEGRITY
        DestinationSQL matchedDest = destinationSqlRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(destinationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Destination not found in catalog database: " + destinationName));

        DestinationSQL foundCatalogDest = destinationSqlRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(destinationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Destination not found: " + destinationName));
        
        TripsSQL sqlTrip = new TripsSQL();
        sqlTrip.setStartDate(LocalDate.parse(startDateStr));
        sqlTrip.setEndDate(LocalDate.parse(endDateStr));
        sqlTrip.setMaxTravelers(maxTravelers);
        
        sqlTrip.getDestination().add(matchedDest);
        matchedDest.setTrip(sqlTrip);
        
        TripsSQL savedTrip = tripSqlRepository.save(sqlTrip); // Save parent first!
        foundCatalogDest.setTrip(savedTrip);
        destinationSqlRepository.save(foundCatalogDest);

        // 2. CONSTRUCT NON-RELATIONAL DATA (Firestore Document)
        TripDoc noSqlTrip = new TripDoc();
        // Generate a clean string tracking ID matching your collection path blueprint
        noSqlTrip.setTripId("TRIP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        noSqlTrip.setDestinationName(destinationName);
        noSqlTrip.setStartDate(startDateStr);
        noSqlTrip.setEndDate(endDateStr);
        noSqlTrip.setUserId(passedUserId); // Wire this dynamically later once your auth module is fully bound

        // 3. PASS TO SYNCHRONIZED TRANSACTION AGENT
        travelMateService.createTripInBothSystems(sqlTrip, noSqlTrip);
        
        firestore.collection("trips").document(noSqlTrip.getTripId()).set(noSqlTrip).get();
        
        return ResponseEntity.ok("Successfully synchronized trip lifecycle records across both database engines!");
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
        
        // Map destinations to match what the React option template expects
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
            pollOption.setTrip(trip); // Bind back to parent structural key
            
            destinationSqlRepository.save(pollOption);
        }

        return getTripPoll(tripId); // Return updated poll layout shape
    }

    /**
     * 3. CAST A VOTE SECURELY (INCREMENT TRANSACTION)
     * Finds the targeted option record line and safely increments its current integer state.
     */
    @PostMapping("/{tripId}/destinations/{destinationId}/vote")
    public ResponseEntity<Map<String, Object>> castVote(
            @PathVariable Long tripId,
            @PathVariable Long destinationId,
            @RequestParam String userId) {
        
        DestinationSQL destination = destinationSqlRepository.findById(destinationId)
            .orElseThrow(() -> new RuntimeException("Target voting option row context not found"));

        // Safe increment operation
        destination.setVotes(destination.getVotes() + 1);
        destinationSqlRepository.save(destination);

        return getTripPoll(tripId); // Return fresh state instantly to push re-render animations
    }
}
