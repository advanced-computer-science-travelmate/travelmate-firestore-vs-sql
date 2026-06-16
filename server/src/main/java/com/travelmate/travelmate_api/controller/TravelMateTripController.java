package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.travelmate.travelmate_api.models.sql.TripsSQL;
import com.travelmate.travelmate_api.repository.sql.TripSQLRepository;

@RestController
@RequestMapping("/api/travel/trips")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateTripController {

    private final TripSQLRepository tripSqlRepository;
    private final Firestore firestore;

    public TravelMateTripController(TripSQLRepository tripSqlRepository, Firestore firestore) {
        this.tripSqlRepository = tripSqlRepository;
        this.firestore = firestore;
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
