package com.travelmate.travelmate_api.repository.nosql;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.ProposalDoc;

public class VotingProposalFirestoreRepository {
	private final Firestore firestore;

    public VotingProposalFirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public void save(ProposalDoc proposal) throws Exception {
        firestore.collection("proposals").document(proposal.getProposalId()).set(proposal).get();
    }

    public ProposalDoc findById(String proposalId) throws Exception {
        var doc = firestore.collection("proposals").document(proposalId).get().get();
        return doc.exists() ? doc.toObject(ProposalDoc.class) : null;
    }

    public List<ProposalDoc> findAll() throws Exception {
        var future = firestore.collection("proposals").get();
        List<ProposalDoc> list = new ArrayList<>();
        for (var doc : future.get().getDocuments()) {
            list.add(doc.toObject(ProposalDoc.class));
        }
        return list;
    }

    public void update(String proposalId, ProposalDoc proposalUpdates) throws Exception {
        firestore.collection("proposals").document(proposalId).set(proposalUpdates, SetOptions.merge()).get();
    }

    public void castVote(String proposalId, String voterName) throws Exception {
        firestore.collection("proposals").document(proposalId)
                 .update("currentVotes", FieldValue.arrayUnion(voterName)).get();
    }

    public void deleteById(String proposalId) throws Exception {
        firestore.collection("proposals").document(proposalId).delete().get();
    }
}
