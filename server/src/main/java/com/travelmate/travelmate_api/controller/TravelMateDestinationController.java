package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.travelmate.travelmate_api.models.nosql.DestinationDoc;
import com.travelmate.travelmate_api.models.sql.BookingsSQL;
import com.travelmate.travelmate_api.models.sql.DestinationSQL;
import com.travelmate.travelmate_api.repository.sql.BookingSQLRepository;
import com.travelmate.travelmate_api.repository.sql.DestinationSQLRepository;

@RestController
@RequestMapping("/api/travel/destinations")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateDestinationController {

	private final DestinationSQLRepository destinationSqlRepository;
    private final BookingSQLRepository bookingRepository; // Added for the hotel booking lookup
    private final Firestore firestore;
    private static final String COLLECTION = "destinations";

    // All repositories and Firestore instances safely co-exist in the constructor
    public TravelMateDestinationController(DestinationSQLRepository destinationSqlRepository, 
                                             BookingSQLRepository bookingRepository, 
                                             Firestore firestore) {
        this.destinationSqlRepository = destinationSqlRepository;
        this.bookingRepository = bookingRepository;
        this.firestore = firestore;
    }

    @GetMapping("/sql")
    public ResponseEntity<List<DestinationSQL>> getAllDestinationsSQL() {
        return ResponseEntity.ok(destinationSqlRepository.findAll());
    }
    
    @GetMapping("/firestore")
    public ResponseEntity<List<DestinationDoc>> getAllDestinationsNoSQL() throws Exception {
        var future = firestore.collection(COLLECTION).get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<DestinationDoc> list = new ArrayList<>();
        for (var doc : docs) {
            list.add(doc.toObject(DestinationDoc.class));
        }
        return ResponseEntity.ok(list);
    }

    // ==========================================
    // NEW FRONTEND INTEGRATION ENDPOINTS
    // ==========================================

    @GetMapping("/europe")
    public ResponseEntity<List<DestinationDoc>> getEuropeanDestinations() throws Exception {
        var future = firestore.collection(COLLECTION).get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<DestinationDoc> list = new ArrayList<>();
        
        for (var doc : docs) {
            list.add(doc.toObject(DestinationDoc.class));
        }
        
        // Serves virtual fields (famousCities/famousPlaces) seamlessly to React
        return ResponseEntity.ok(list);
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<Map<String, Object>>> getHotelsFromBookings(@RequestParam String location) {
        List<BookingsSQL> allBookings = bookingRepository.findAll();
        
        List<Map<String, Object>> hotelList = allBookings.stream()
            .filter(b -> "HOTEL".equalsIgnoreCase(b.getBookingType()))
            .filter(b -> b.getConfirmationNumber() != null && b.getConfirmationNumber().toUpperCase().contains(location.toUpperCase()))
            .map(b -> {
                Map<String, Object> hotelMap = new HashMap<>();
                hotelMap.put("id", "hotel-bk-" + b.getId());
                hotelMap.put("name", b.getConfirmationNumber().startsWith("CONF-") ? "Luxury Stay " + location : b.getConfirmationNumber());
                hotelMap.put("address", location + " Resort District");
                hotelMap.put("mapsUrl", "https://maps.google.com/?q=" + location);
                return hotelMap;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(hotelList);
    }
}
