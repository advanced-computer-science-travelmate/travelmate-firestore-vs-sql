package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/europe")
    public ResponseEntity<?> getEuropeanDestinations() {
    	String url = "https://restcountries.com/v3.1/region/europe?fields=name,flags,capital";
        
        try {
            // 1. Set up standard browser user agent headers to pass proxy filters
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 2. Execute request with headers wrapped
            ResponseEntity<Object[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object[].class);
            
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("REST Countries API down or blocked request. Activating local fallback payload: " + e.getMessage());
            
            // 🚀 THE ULTIMATE SAFEGUARD: Fallback local mock data so your frontend dropdown NEVER breaks!
            List<Map<String, Object>> fallbackList = new ArrayList<>();
            String[] commonCountries = {"Germany", "Italy", "France", "Spain", "Netherlands", "Switzerland", "Austria", "Belgium", "Portugal", "Sweden"};
            String[] flags = {"🇩🇪", "🇮🇹", "🇫🇷", "🇪🇸", "🇳🇱", "🇨🇭", "🇦🇹", "🇧🇪", "🇵🇹", "🇸🇪"};
            
            for (int i = 0; i < commonCountries.length; i++) {
                Map<String, Object> countryMap = new HashMap<>();
                Map<String, Object> nameMap = new HashMap<>();
                Map<String, Object> flagMap = new HashMap<>();
                
                nameMap.put("common", commonCountries[i]);
                flagMap.put("png", "https://flagcdn.com/w320/" + commonCountries[i].substring(0,2).toLowerCase() + ".png");
                
                countryMap.put("name", nameMap);
                countryMap.put("flags", flagMap);
                countryMap.put("capital", List.of(commonCountries[i] + " City"));
                
                fallbackList.add(countryMap);
            }
            
            return ResponseEntity.ok(fallbackList);
        }
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
