package com.travelmate.travelmate_api.dto;

import java.util.List;

public class TripRequestDTO {
	private String startDate;
    private String endDate;
    private int maxTravelers;
    private int adults;
    private int children;
    private int rooms;
    private String destinationName;
    private String sqlUserId;
    private String bookingType;
    private String confirmationNumber;
    private double price;
    private String proposalTitle;
    private String proposalStatus;
    private int votesNeeded;
    private double estimatedPrice;
    private List<String> voterNames;

    // Getters and Setters
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMaxTravelers() { return maxTravelers; }
    public void setMaxTravelers(int maxTravelers) { this.maxTravelers = maxTravelers; }

    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }

    public int getChildren() { return children; }
    public void setChildren(int children) { this.children = children; }

    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getSqlUserId() { return sqlUserId; }
    public void setSqlUserId(String sqlUserId) { this.sqlUserId = sqlUserId; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public String getConfirmationNumber() { return confirmationNumber; }
    public void setConfirmationNumber(String confirmationNumber) { this.confirmationNumber = confirmationNumber; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getProposalTitle() { return proposalTitle; }
    public void setProposalTitle(String proposalTitle) { this.proposalTitle = proposalTitle; }

    public String getProposalStatus() { return proposalStatus; }
    public void setProposalStatus(String proposalStatus) { this.proposalStatus = proposalStatus; }

    public int getVotesNeeded() { return votesNeeded; }
    public void setVotesNeeded(int votesNeeded) { this.votesNeeded = votesNeeded; }

    public double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(double estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public List<String> getVoterNames() { return voterNames; }
    public void setVoterNames(List<String> voterNames) { this.voterNames = voterNames; }
}
