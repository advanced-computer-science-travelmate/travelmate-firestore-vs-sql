package com.travelmate.travelmate_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.ProposalDoc;
import com.travelmate.travelmate_api.models.sql.VotingProposalSQL;
//import com.travelmate.travelmate_api.models.firestore.ProposalDoc;
import com.travelmate.travelmate_api.repository.sql.VotingProposalSQLRepository;

@RestController
@RequestMapping("/api/travel/proposals")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateVotingProposalController {

    private final VotingProposalSQLRepository proposalSqlRepository;
    private final Firestore firestore;

    public TravelMateVotingProposalController(VotingProposalSQLRepository proposalSqlRepository, Firestore firestore) {
        this.proposalSqlRepository = proposalSqlRepository;
        this.firestore = firestore;
    }

    // ==========================================
    // CLOUD SQL PATHS (Relational)
    // ==========================================

    @PostMapping("/sql")
    public ResponseEntity<VotingProposalSQL> createProposalSQL(@RequestBody VotingProposalSQL proposal) {
        return ResponseEntity.ok(proposalSqlRepository.save(proposal));
    }

    @GetMapping("/sql")
    public ResponseEntity<List<VotingProposalSQL>> getAllProposalsSQL() {
        return ResponseEntity.ok(proposalSqlRepository.findAll());
    }

    @PutMapping("/sql/{id}")
    public ResponseEntity<VotingProposalSQL> updateProposalSQL(@PathVariable Long id, @RequestBody VotingProposalSQL details) {
        return proposalSqlRepository.findById(id).map(prop -> {
            prop.setTitle(details.getTitle());
            prop.setStatus(details.getStatus());
            prop.setCurrentVotes(details.getCurrentVotes());
            return ResponseEntity.ok(proposalSqlRepository.save(prop));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/sql/{id}")
    public ResponseEntity<Void> deleteProposalSQL(@PathVariable Long id) {
        if (proposalSqlRepository.existsById(id)) {
            proposalSqlRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // CLOUD FIRESTORE PATHS (NoSQL)
    // ==========================================

    @PostMapping("/firestore")
    public ResponseEntity<String> createProposalNoSQL(@RequestBody ProposalDoc proposal) throws Exception {
        firestore.collection("proposals").document(proposal.getProposalId()).set(proposal).get();
        return ResponseEntity.ok("Proposal document saved to Firestore");
    }

    @PostMapping("/firestore/{id}/vote")
    public ResponseEntity<String> voteNoSQL(@PathVariable String id, @RequestParam String voterName) throws Exception {
        firestore.collection("proposals").document(id)
                 .update("currentVotes", FieldValue.arrayUnion(voterName)).get();
        return ResponseEntity.ok("Vote logged instantly via high-speed native NoSQL array union operations");
    }

    @PutMapping("/firestore/{id}")
    public ResponseEntity<String> updateProposalNoSQL(@PathVariable String id, @RequestBody ProposalDoc proposal) throws Exception {
        firestore.collection("proposals").document(id).set(proposal, SetOptions.merge()).get();
        return ResponseEntity.ok("Proposal fields mutated successfully");
    }

    @DeleteMapping("/firestore/{id}")
    public ResponseEntity<String> deleteProposalNoSQL(@PathVariable String id) throws Exception {
        firestore.collection("proposals").document(id).delete().get();
        return ResponseEntity.ok("Proposal object scrubbed from Firestore instances");
    }
}