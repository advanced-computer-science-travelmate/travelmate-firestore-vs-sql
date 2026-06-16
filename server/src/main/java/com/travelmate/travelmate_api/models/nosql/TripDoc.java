package com.travelmate.travelmate_api.models.nosql;

public class TripDoc {
	private String tripId;
    private String destinationName;
    private String startDate;
    private String endDate;
    private String userId;
    
    private int maxTravelers;
    
	public String getTripId() {
		return tripId;
	}
	public void setTripId(String tripId) {
		this.tripId = tripId;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getMaxTravelers() {
		return maxTravelers;
	}
	public void setMaxTravelers(int maxTravelers) {
		this.maxTravelers = maxTravelers;
	}
    
    
}
