package com.travelmate.travelmate_api.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.models.nosql.TravelmateItinerary;
import com.travelmate.travelmate_api.repository.nosql.TravelMateItineraryRepository;

@RestController
@RequestMapping("/api/travel/itineraries")
public class TravelmateItineraryController {
	private final TravelMateItineraryRepository itineraryRepository;
	private Firestore firestore;

    public TravelmateItineraryController(TravelMateItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    
    @PostMapping
    public String createItinerary(@RequestBody TravelmateItinerary itinerary) {
        try {
            itinerary.setCreatedAt(System.currentTimeMillis());
            return itineraryRepository.saveItinerary(itinerary);
        } catch (Exception e) {
            return "Firestore saving failed: " + e.getMessage();
        }
    }

    @GetMapping("/{id}")
    public Object getItinerary(@PathVariable String id) {
    	try {
            return itineraryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found!"));
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    @PutMapping("/update-itinerary/{id}")
    public String updateItinerary(@PathVariable String id, @RequestBody TravelmateItinerary updatedItinerary) {
        try {
        	updatedItinerary.setId(id);
            updatedItinerary.setCreatedAt(System.currentTimeMillis());
            itineraryRepository.saveItinerary(updatedItinerary);
            return "Success! Itinerary document " + id + " has been fully updated in Firestore.";
            
        } catch (Exception e) {
            return "Firestore update failed: " + e.getMessage();
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public String deleteItinerary(@PathVariable String id) {
        try {
            String deletedId = itineraryRepository.deleteItinerary(id);
            return "Success! Itinerary document " + id + " has been deleted from Firestore.";
        } catch (Exception e) {
            return "Firestore deletion failed: " + e.getMessage();
        }
    }
}
