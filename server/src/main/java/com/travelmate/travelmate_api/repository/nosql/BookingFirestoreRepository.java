package com.travelmate.travelmate_api.repository.nosql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.BookingDoc;

@Repository
public class BookingFirestoreRepository {
	private final Firestore firestore;

    public BookingFirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public void save(BookingDoc booking) throws Exception {
        firestore.collection("bookings").document(booking.getBookingId()).set(booking).get();
    }

    public BookingDoc findById(String bookingId) throws Exception {
        var doc = firestore.collection("bookings").document(bookingId).get().get();
        return doc.exists() ? doc.toObject(BookingDoc.class) : null;
    }

    public List<BookingDoc> findAll() throws Exception {
        var future = firestore.collection("bookings").get();
        List<BookingDoc> list = new ArrayList<>();
        for (var doc : future.get().getDocuments()) {
            list.add(doc.toObject(BookingDoc.class));
        }
        return list;
    }

    public void update(String bookingId, BookingDoc bookingUpdates) throws Exception {
        firestore.collection("bookings").document(bookingId).set(bookingUpdates, SetOptions.merge()).get();
    }

    public void deleteById(String bookingId) throws Exception {
        firestore.collection("bookings").document(bookingId).delete().get();
    }
}
