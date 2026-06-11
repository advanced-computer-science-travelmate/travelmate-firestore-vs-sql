package com.travelmate.travelmate_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelmate.travelmate_api.service.TravelMateService;

@RestController
@RequestMapping("/api/travel/proposals")
@CrossOrigin(origins = "http://localhost:3000")
public class TravelMateProposalController {

    private final TravelMateService travelService;

    public TravelMateProposalController(TravelMateService travelService) {
        this.travelService = travelService;
    }

    // CREATE a new collaborative proposal open for votes
    @PostMapping
    public String launchProposal(@RequestParam String itineraryId,
                                 @RequestParam String title,
                                 @RequestParam Double price,
                                 @RequestParam int votesNeeded) {
        try {
            return travelService.createBookingProposal(itineraryId, title, price, votesNeeded);
        } catch (Exception e) {
            return "Failed to create proposal: " + e.getMessage();
        }
    }

    // POST a vote from a group friend to check for consensus
    @PostMapping("/{proposalId}/vote")
    public String voteOnProposal(@PathVariable String proposalId,
                                 @RequestParam String friendName,
                                 @RequestParam Long userId) {
        try {
            return travelService.castVoteOnProposal(proposalId, friendName, userId);
        } catch (Exception e) {
            return "Error casting vote: " + e.getMessage();
        }
    }

    // PUT a real-time message or chat comment into the shared itinerary log
    @PutMapping("/itinerary/{itineraryId}/comment")
    public String addGroupComment(@PathVariable String itineraryId, 
                                  @RequestParam String friendName, 
                                  @RequestParam String comment) {
        try {
            return travelService.addFriendCommentToItinerary(itineraryId, friendName, comment);
        } catch (Exception e) {
            return "Comment update failed: " + e.getMessage();
        }
    }
}
