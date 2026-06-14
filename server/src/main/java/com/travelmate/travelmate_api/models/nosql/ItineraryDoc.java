package com.travelmate.travelmate_api.models.nosql;

import java.util.List;

public class ItineraryDoc {
    private String itineraryId;
    private String destination;
    private String startDate;
    private String endDate;
    private Integer maxTravelers;
    private List<String> activities;

    public ItineraryDoc() {}

    public String getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public Integer getMaxTravelers() {
        return maxTravelers;
    }

    public void setMaxTravelers(Integer maxTravelers) {
        this.maxTravelers = maxTravelers;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
}