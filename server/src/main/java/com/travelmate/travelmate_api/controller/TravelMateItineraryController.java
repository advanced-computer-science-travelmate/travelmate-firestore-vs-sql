package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.List;

//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.SetOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.ItineraryDoc;
import com.travelmate.travelmate_api.models.sql.ItinerarySQL;
import com.travelmate.travelmate_api.repository.sql.ItinerarySQLRepository;

@RestController
@RequestMapping("/api/travel/itineraries")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateItineraryController {

    private final ItinerarySQLRepository itinerarySqlRepository;
    private final Firestore firestore;

    public TravelMateItineraryController(ItinerarySQLRepository itinerarySqlRepository, Firestore firestore) {
        this.itinerarySqlRepository = itinerarySqlRepository;
        this.firestore = firestore;
    }

    // ==========================================
    // CLOUD SQL PATHS (Relational)
    // ==========================================

    @PostMapping("/sql")
    public ResponseEntity<ItinerarySQL> createItinerarySQL(@RequestBody ItinerarySQL itinerary) {
        return ResponseEntity.ok(itinerarySqlRepository.save(itinerary));
    }

    @GetMapping("/sql")
    public ResponseEntity<List<ItinerarySQL>> getAllItinerariesSQL() {
        return ResponseEntity.ok(itinerarySqlRepository.findAll());
    }

    @PutMapping("/sql/{id}")
    public ResponseEntity<ItinerarySQL> updateItinerarySQL(@PathVariable Long id, @RequestBody ItinerarySQL details) {
        return itinerarySqlRepository.findById(id).map(itin -> {
            itin.setDestination(details.getDestination());
            itin.setStartDate(details.getStartDate());
            itin.setEndDate(details.getEndDate());
            itin.setMaxTravelers(details.getMaxTravelers());
            itin.setActivities(details.getActivities());
            return ResponseEntity.ok(itinerarySqlRepository.save(itin));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/sql/{id}")
    public ResponseEntity<Void> deleteItinerarySQL(@PathVariable Long id) {
        if (itinerarySqlRepository.existsById(id)) {
            itinerarySqlRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // CLOUD FIRESTORE PATHS (NoSQL)
    // ==========================================

    @PostMapping("/firestore")
    public ResponseEntity<String> createItineraryNoSQL(@RequestBody ItineraryDoc itinerary) throws Exception {
        firestore.collection("itineraries").document(itinerary.getItineraryId()).set(itinerary).get();
        return ResponseEntity.ok("Itinerary structured in Firestore");
    }

    @GetMapping("/firestore")
    public ResponseEntity<List<ItineraryDoc>> getAllItinerariesNoSQL() throws Exception {
        var future = firestore.collection("itineraries").get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<ItineraryDoc> list = new ArrayList<>();
        for (var doc : docs) {
            list.add(doc.toObject(ItineraryDoc.class));
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/firestore/{id}")
    public ResponseEntity<String> updateItineraryNoSQL(@PathVariable String id, @RequestBody ItineraryDoc itinerary) throws Exception {
        firestore.collection("itineraries").document(id).set(itinerary, SetOptions.merge()).get();
        return ResponseEntity.ok("Itinerary document merged in Firestore");
    }

    @DeleteMapping("/firestore/{id}")
    public ResponseEntity<String> deleteItineraryNoSQL(@PathVariable String id) throws Exception {
        firestore.collection("itineraries").document(id).delete().get();
        return ResponseEntity.ok("Itinerary document dropped from Firestore");
    }
}