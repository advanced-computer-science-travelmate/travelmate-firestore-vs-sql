package com.travelmate.travelmate_api.models.nosql;

import java.util.List;

public class ProposalDoc {
    private String proposalId;
    private String itineraryId;
    private String title;
    private Double estimatedPrice;
    private Integer votesNeeded;
    private String status;
    private List<String> currentVotes;

    public ProposalDoc() {}

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public Integer getVotesNeeded() {
        return votesNeeded;
    }

    public void setVotesNeeded(Integer votesNeeded) {
        this.votesNeeded = votesNeeded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(List<String> currentVotes) {
        this.currentVotes = currentVotes;
    }
}