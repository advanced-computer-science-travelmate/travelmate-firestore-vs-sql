package com.travelmate.travelmate_api.controller;

import java.util.List;

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
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.BookingDoc;
import com.travelmate.travelmate_api.models.sql.BookingsSQL;
//import com.travelmate.travelmate_api.models.firestore.BookingDoc;
import com.travelmate.travelmate_api.repository.sql.BookingSQLRepository;

@RestController
@RequestMapping("/api/travel/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateBookingController {

    private final BookingSQLRepository bookingSqlRepository;
    private final Firestore firestore;

    public TravelMateBookingController(BookingSQLRepository bookingSqlRepository, Firestore firestore) {
        this.bookingSqlRepository = bookingSqlRepository;
        this.firestore = firestore;
    }

    // ==========================================
    // CLOUD SQL PATHS (Relational)
    // ==========================================

    @PostMapping("/sql")
    public ResponseEntity<BookingsSQL> createBookingSQL(@RequestBody BookingsSQL booking) {
        return ResponseEntity.ok(bookingSqlRepository.save(booking));
    }

    @GetMapping("/sql")
    public ResponseEntity<List<BookingsSQL>> getAllBookingsSQL() {
        return ResponseEntity.ok(bookingSqlRepository.findAll());
    }

    @PutMapping("/sql/{id}")
    public ResponseEntity<BookingsSQL> updateBookingSQL(@PathVariable Long id, @RequestBody BookingsSQL details) {
        return bookingSqlRepository.findById(id).map(booking -> {
            booking.setBookingType(details.getBookingType());
            booking.setPrice(details.getPrice());
            booking.setConfirmationNumber(details.getConfirmationNumber());
            return ResponseEntity.ok(bookingSqlRepository.save(booking));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/sql/{id}")
    public ResponseEntity<Void> deleteBookingSQL(@PathVariable Long id) {
        if (bookingSqlRepository.existsById(id)) {
            bookingSqlRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // CLOUD FIRESTORE PATHS (NoSQL)
    // ==========================================

    @PostMapping("/firestore")
    public ResponseEntity<String> createBookingNoSQL(@RequestBody BookingDoc booking) throws Exception {
        firestore.collection("bookings").document(booking.getBookingId()).set(booking).get();
        return ResponseEntity.ok("Booking recorded in Firestore");
    }

    @GetMapping("/firestore/{id}")
    public ResponseEntity<BookingDoc> getBookingNoSQL(@PathVariable String id) throws Exception {
        var doc = firestore.collection("bookings").document(id).get().get();
        return doc.exists() ? ResponseEntity.ok(doc.toObject(BookingDoc.class)) : ResponseEntity.notFound().build();
    }

    @PutMapping("/firestore/{id}")
    public ResponseEntity<String> updateBookingNoSQL(@PathVariable String id, @RequestBody BookingDoc booking) throws Exception {
        firestore.collection("bookings").document(id).set(booking, SetOptions.merge()).get();
        return ResponseEntity.ok("Booking updated in Firestore");
    }

    @DeleteMapping("/firestore/{id}")
    public ResponseEntity<String> deleteBookingNoSQL(@PathVariable String id) throws Exception {
        firestore.collection("bookings").document(id).delete().get();
        return ResponseEntity.ok("Booking document dropped from Firestore");
    }
}