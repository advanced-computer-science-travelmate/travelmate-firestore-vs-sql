package com.travelmate.travelmate_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelmate.travelmate_api.models.sql.Bookings;
import com.travelmate.travelmate_api.service.TravelMateService;

@RestController
@RequestMapping("/api/travel/bookings")
@CrossOrigin(origins = "http://localhost:3000") // Ready for your React frontend!
public class TravelMateBookingController {

	    private final TravelMateService travelService;

	    public TravelMateBookingController(TravelMateService travelService) {
	        this.travelService = travelService;
	    }

	 // CREATE a manual booking straight to SQL & Firestore timeline
	    @PostMapping("/book")
	    public String bookTrip(@RequestParam Long userId,
	                           @RequestParam String itineraryId,
	                           @RequestParam String type,
	                           @RequestParam Double price) {
	        try {
	            return travelService.executeHybridBooking(userId, itineraryId, type, price);
	        } catch (Exception e) {
	            return "Booking failed: " + e.getMessage();
	        }
	    }

	    // READ all SQL bookings for a specific user
	    @GetMapping("/bookings/{userId}")
	    public List<Bookings> getUserBookings(@PathVariable Long userId) {
	        return travelService.getUserBookings(userId);
	    }

	    // UPDATE a booking's core data status in MySQL
	    // Endpoint: PUT http://localhost:8080/api/travel/booking/1?newType=HOTEL_PAID
	    @PutMapping("/{bookingId}")
	    public String updateBooking(@PathVariable Long bookingId, @RequestParam String newType) {
	        return travelService.updateBookingType(bookingId, newType);
	    }
}
