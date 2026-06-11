package com.travelmate.travelmate_api.repository.nosql;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.travelmate.travelmate_api.models.nosql.TravelmateItinerary;

@Repository
public class TravelMateItineraryRepository {
	private final Firestore firestore;

    // Spring Boot automatically injects the native Firestore client bean here
    public TravelMateItineraryRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Saves or updates an itinerary document directly inside the "itineraries" collection
     */
    public String saveItinerary(TravelmateItinerary itinerary) throws Exception {
        // If no document ID is provided, create a unique timestamp-driven key
        if (itinerary.getId() == null || itinerary.getId().trim().isEmpty()) {
            itinerary.setId("itin_" + System.currentTimeMillis());
        }

        // Pushes the entire Java POJO directly to your travelmate-nosql cloud instance
        ApiFuture<WriteResult> future = firestore.collection("itineraries")
                .document(itinerary.getId())
                .set(itinerary);

        // Blocks synchronously to ensure the cloud confirms writing before returning
        future.get();
        return itinerary.getId();
    }

    /**
     * Retrieves an itinerary by its unique document string identifier
     */
    public Optional<TravelmateItinerary> findById(String id) throws Exception {
        ApiFuture<DocumentSnapshot> future = firestore.collection("itineraries").document(id).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            // Natively converts the Firestore document map back into a clean Itinerary Java object shell
            TravelmateItinerary itinerary = document.toObject(TravelmateItinerary.class);
            return Optional.ofNullable(itinerary);
        }
        return Optional.empty();
    }

	public String deleteItinerary(String id) throws Exception {
        firestore.collection("itineraries").document(id).delete().get();
        return id;
    }
}
