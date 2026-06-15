package com.travelmate.travelmate_api.repository.nosql;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.UserDoc;

@Repository
	public class UserFirestoreRepository {

	    private final Firestore firestore;

	    public UserFirestoreRepository(Firestore firestore) {
	        this.firestore = firestore;
	    }

	    public void save(UserDoc user) throws Exception {
	        firestore.collection("users").document(user.getUserId()).set(user).get();
	    }

	    public UserDoc findById(String userId) throws Exception {
	        var doc = firestore.collection("users").document(userId).get().get();
	        return doc.exists() ? doc.toObject(UserDoc.class) : null;
	    }

	    public void update(String userId, UserDoc userUpdates) throws Exception {
	        firestore.collection("users").document(userId).set(userUpdates, SetOptions.merge()).get();
	    }

	    public void deleteById(String userId) throws Exception {
	        firestore.collection("users").document(userId).delete().get();
	    }
	}

