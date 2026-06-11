package com.travelmate.travelmate_api.models.nosql;

import java.util.List;

import com.google.cloud.firestore.Firestore;

//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

public class TravelmateItinerary {
	private String id;          // Firestore Document ID
    private Long userId;        // MySQL User ID
    private String destination;
    private List<String> activities;
    private Long createdAt;
    
    private Firestore firestore;
    
    public TravelmateItinerary() {
    	
    }
    
	public TravelmateItinerary(String id, Long userId, String destination, List<String> activities, Long createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.destination = destination;
		this.activities = activities;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<String> getActivities() {
		return activities;
	}

	public void setActivities(List<String> activities) {
		this.activities = activities;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
}
