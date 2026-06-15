package com.travelmate.travelmate_api.repository.nosql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.BudgetDoc;

@Repository
public class BudgetFirestoreRepository {
	private final Firestore firestore;

    public BudgetFirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public void save(String budgetId, BudgetDoc budget) throws Exception {
        firestore.collection("budgets").document(budgetId).set(budget).get();
    }

    public BudgetDoc findById(String budgetId) throws Exception {
        var doc = firestore.collection("budgets").document(budgetId).get().get();
        return doc.exists() ? doc.toObject(BudgetDoc.class) : null;
    }

    public List<BudgetDoc> findAll() throws Exception {
        var future = firestore.collection("budgets").get();
        List<BudgetDoc> list = new ArrayList<>();
        for (var doc : future.get().getDocuments()) {
            list.add(doc.toObject(BudgetDoc.class));
        }
        return list;
    }

    public void update(String budgetId, BudgetDoc budgetUpdates) throws Exception {
        firestore.collection("budgets").document(budgetId).set(budgetUpdates, SetOptions.merge()).get();
    }

    public void deleteById(String budgetId) throws Exception {
        firestore.collection("budgets").document(budgetId).delete().get();
    }
}
