package com.travelmate.travelmate_api.repository.nosql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.ItineraryDoc;

@Repository
public class ItineraryFirestoreRepository {
	private final Firestore firestore;

    public ItineraryFirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public void save(ItineraryDoc itinerary) throws Exception {
        firestore.collection("itineraries").document(itinerary.getItineraryId()).set(itinerary).get();
    }

    public ItineraryDoc findById(String itineraryId) throws Exception {
        var doc = firestore.collection("itineraries").document(itineraryId).get().get();
        return doc.exists() ? doc.toObject(ItineraryDoc.class) : null;
    }

    public List<ItineraryDoc> findAll() throws Exception {
        var future = firestore.collection("itineraries").get();
        List<ItineraryDoc> list = new ArrayList<>();
        for (var doc : future.get().getDocuments()) {
            list.add(doc.toObject(ItineraryDoc.class));
        }
        return list;
    }

    public void update(String itineraryId, ItineraryDoc itineraryUpdates) throws Exception {
        firestore.collection("itineraries").document(itineraryId).set(itineraryUpdates, SetOptions.merge()).get();
    }

    public void appendActivity(String itineraryId, String activityText) throws Exception {
        firestore.collection("itineraries").document(itineraryId)
                 .update("activities", FieldValue.arrayUnion(activityText)).get();
    }

    public void deleteById(String itineraryId) throws Exception {
        firestore.collection("itineraries").document(itineraryId).delete().get();
    }
}
