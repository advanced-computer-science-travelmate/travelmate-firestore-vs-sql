package com.travelmate.travelmate_api.models.sql;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="itineraries")
public class ItinerarySQL {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_travelers")
    private Integer maxTravelers;

    @ElementCollection
    @CollectionTable(name = "itinerary_activities", joinColumns = @JoinColumn(name = "itinerary_id"))
    @Column(name = "activity")
    private List<String> activities;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotingProposalSQL> proposals;

    public ItinerarySQL() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getMaxTravelers() { return maxTravelers; }
    public void setMaxTravelers(Integer maxTravelers) { this.maxTravelers = maxTravelers; }
    public List<String> getActivities() { return activities; }
    public void setActivities(List<String> activities) { this.activities = activities; }
    public List<VotingProposalSQL> getProposals() { return proposals; }
    public void setProposals(List<VotingProposalSQL> proposals) { this.proposals = proposals; }
}
