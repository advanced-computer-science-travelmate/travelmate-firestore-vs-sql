package com.travelmate.travelmate_api.models.sql;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="voting_proposals")
public class VotingProposalSQL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "estimated_price", nullable = false)
    private Double estimatedPrice;

    @Column(name = "votes_needed", nullable = false)
    private Integer votesNeeded;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, COMPLETED

    // FIX 1: Pointing to the correct singular class type: TripSQL
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripsSQL trip;

    @ElementCollection
    @CollectionTable(name = "proposal_voters", joinColumns = @JoinColumn(name = "proposal_id"))
    @Column(name = "voter_name")
    private List<String> currentVotes;

    public VotingProposalSQL() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(Double estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    public Integer getVotesNeeded() { return votesNeeded; }
    public void setVotesNeeded(Integer votesNeeded) { this.votesNeeded = votesNeeded; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // FIX 2: Updated relationship getter and setter to use the new TripSQL reference
    public TripsSQL getTrip() { return trip; }
    public void setTrip(TripsSQL trip) { this.trip = trip; }
    
    public List<String> getCurrentVotes() { return currentVotes; }
    public void setCurrentVotes(List<String> currentVotes) { this.currentVotes = currentVotes; }
}