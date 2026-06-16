package com.travelmate.travelmate_api.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    @PostMapping("/create")
    public ResponseEntity<String> createTrip(@RequestBody Map<String, Object> payload) throws Exception {
        
        String destinationName = (String) payload.get("destinationName");
        String startDateStr = (String) payload.get("startDate");
        String endDateStr = (String) payload.get("endDate");
        int maxTravelers = (Integer) payload.get("maxTravelers");

        
        DestinationSQL matchedDest = destinationSqlRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(destinationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Destination not found in catalog database: " + destinationName));

        TripsSQL sqlTrip = new TripsSQL();
        sqlTrip.setDestination(matchedDest);
        sqlTrip.setStartDate(LocalDate.parse(startDateStr));
        sqlTrip.setEndDate(LocalDate.parse(endDateStr));
        sqlTrip.setMaxTravelers(maxTravelers);

        // 2. CONSTRUCT NON-RELATIONAL DATA (Firestore Document)
        TripDoc noSqlTrip = new TripDoc();
        // Generate a clean string tracking ID matching your collection path blueprint
        noSqlTrip.setTripId("TRIP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        noSqlTrip.setDestinationName(destinationName);
        noSqlTrip.setStartDate(startDateStr);
        noSqlTrip.setEndDate(endDateStr);
        noSqlTrip.setUserId("USER-ACTIVE-101"); // Wire this dynamically later once your auth module is fully bound

        // 3. PASS TO SYNCHRONIZED TRANSACTION AGENT
        travelMateService.createTripInBothSystems(sqlTrip, noSqlTrip);

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
}
