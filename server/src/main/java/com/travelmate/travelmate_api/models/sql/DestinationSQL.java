package com.travelmate.travelmate_api.models.sql;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "destinations")
public class DestinationSQL {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Added to support your teammate's UI template fields
    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "image_url")
    private String image;

    @ElementCollection
    @CollectionTable(name = "destination_activities", joinColumns = @JoinColumn(name = "destination_id"))
    @Column(name = "activity")
    private List<String> activities;
    
    @ManyToOne
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    @JsonBackReference
    private TripsSQL trip;

    // 🚀 ADD THIS COLUMN TO TRACK VOTES DYNAMICALLY PER TRIP POLL
    @Column(name = "votes", columnDefinition = "int default 0")
    private int votes;

    @Column(name = "country_code", length = 5)
    private String countryCode;
    
    public DestinationSQL() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<String> getActivities() { return activities; }
    public void setActivities(List<String> activities) { this.activities = activities; }

	public TripsSQL getTrip() {
		return trip;
	}

	public void setTrip(TripsSQL trip) {
		this.trip = trip;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	
    
}
