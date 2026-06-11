package com.travelmate.travelmate_api.models.sql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Bookings {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookingType; // e.g., FLIGHT, HOTEL, TRAIN

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, unique = true)
    private String confirmationNumber;

    // Relational Foreign Key link mapping many bookings back to one specific User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Bookings() {}

    public Bookings(String bookingType, Double price, String confirmationNumber, User user) {
        this.bookingType = bookingType;
        this.price = price;
        this.confirmationNumber = confirmationNumber;
        this.user = user;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBookingType() {
		return bookingType;
	}

	public void setBookingType(String bookingType) {
		this.bookingType = bookingType;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    
}
