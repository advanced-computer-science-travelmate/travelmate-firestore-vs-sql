package com.travelmate.travelmate_api.repository.nosql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.TripDoc;

@Repository
public class TripFirestoreRepository {
    private final Firestore firestore;
    private static final String COLLECTION = "trips";

    public TripFirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public void save(TripDoc trip) throws Exception {
        firestore.collection(COLLECTION).document(trip.getTripId()).set(trip).get();
    }

    public TripDoc findById(String tripId) throws Exception {
        var doc = firestore.collection(COLLECTION).document(tripId).get().get();
        return doc.exists() ? doc.toObject(TripDoc.class) : null;
    }

    public List<TripDoc> findAll() throws Exception {
        var future = firestore.collection(COLLECTION).get();
        List<TripDoc> list = new ArrayList<>();
        for (var doc : future.get().getDocuments()) {
            list.add(doc.toObject(TripDoc.class));
        }
        return list;
    }

    public void update(String tripId, TripDoc tripUpdates) throws Exception {
        firestore.collection(COLLECTION).document(tripId).set(tripUpdates, SetOptions.merge()).get();
    }

    public void appendActivity(String tripId, String activityText) throws Exception {
        firestore.collection(COLLECTION).document(tripId)
                 .update("activities", FieldValue.arrayUnion(activityText)).get();
    }

    public void deleteById(String tripId) throws Exception {
        firestore.collection(COLLECTION).document(tripId).delete().get();
    }
}
