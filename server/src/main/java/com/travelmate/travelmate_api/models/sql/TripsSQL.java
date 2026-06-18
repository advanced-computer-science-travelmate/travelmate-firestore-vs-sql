package com.travelmate.travelmate_api.models.sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")
public class TripsSQL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DestinationSQL> destination = new ArrayList<>();

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_travelers")
    private Integer maxTravelers;

    // Retained from your original structure to lock proposals onto a specific live trip instance
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotingProposalSQL> proposals;
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingsSQL> bookings = new ArrayList<>();

    public TripsSQL() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getMaxTravelers() { return maxTravelers; }
    public void setMaxTravelers(Integer maxTravelers) { this.maxTravelers = maxTravelers; }
    public List<VotingProposalSQL> getProposals() { return proposals; }
    public void setProposals(List<VotingProposalSQL> proposals) { this.proposals = proposals; }

	public List<BookingsSQL> getBookings() {
		return bookings;
	}

	public void setBookings(List<BookingsSQL> bookings) {
		this.bookings = bookings;
	}

	public List<DestinationSQL> getDestination() {
		return destination;
	}

	public void setDestination(List<DestinationSQL> destination) {
		this.destination = destination;
	}
	
	
    
}
