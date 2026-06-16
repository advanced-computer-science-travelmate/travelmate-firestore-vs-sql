package com.travelmate.travelmate_api.repository.nosql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.DestinationDoc;

@Repository
public class DestinationFirestoreRepository {
    private final Firestore firestore;
    private static final String COLLECTION = "destinations";

    public DestinationFirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public void save(DestinationDoc destination) throws Exception {
        firestore.collection(COLLECTION).document(destination.getId()).set(destination).get();
    }

    public DestinationDoc findById(String id) throws Exception {
        var doc = firestore.collection(COLLECTION).document(id).get().get();
        return doc.exists() ? doc.toObject(DestinationDoc.class) : null;
    }

    public List<DestinationDoc> findAll() throws Exception {
        var future = firestore.collection(COLLECTION).get();
        List<DestinationDoc> list = new ArrayList<>();
        for (var doc : future.get().getDocuments()) {
            list.add(doc.toObject(DestinationDoc.class));
        }
        return list;
    }

    public void update(String id, DestinationDoc destinationUpdates) throws Exception {
        firestore.collection(COLLECTION).document(id).set(destinationUpdates, SetOptions.merge()).get();
    }

    public void deleteById(String id) throws Exception {
        firestore.collection(COLLECTION).document(id).delete().get();
    }
}
