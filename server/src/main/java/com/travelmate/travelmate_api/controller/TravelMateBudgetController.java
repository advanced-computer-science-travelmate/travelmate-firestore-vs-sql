package com.travelmate.travelmate_api.controller;

import com.travelmate.travelmate_api.models.sql.BudgetSQL;
//import com.travelmate.travelmate_api.models.firestore.BudgetDoc;
import com.travelmate.travelmate_api.repository.sql.BudgetSQLRepository;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel/budgets")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateBudgetController {

    private final BudgetSQLRepository budgetSqlRepository;
    private final Firestore firestore;

    public TravelMateBudgetController(BudgetSQLRepository budgetSqlRepository, Firestore firestore) {
        this.budgetSqlRepository = budgetSqlRepository;
        this.firestore = firestore;
    }

    // ==========================================
    // CLOUD SQL PATHS (Relational)
    // ==========================================

    @PostMapping("/sql")
    public ResponseEntity<BudgetSQL> createBudgetSQL(@RequestBody BudgetSQL budget) {
        return ResponseEntity.ok(budgetSqlRepository.save(budget));
    }

    @GetMapping("/sql")
    public ResponseEntity<List<BudgetSQL>> getAllBudgetsSQL() {
        return ResponseEntity.ok(budgetSqlRepository.findAll());
    }

    @PutMapping("/sql/{id}")
    public ResponseEntity<BudgetSQL> updateBudgetSQL(@PathVariable Long id, @RequestBody BudgetSQL details) {
        return budgetSqlRepository.findById(id).map(budget -> {
            budget.setCategory(details.getCategory());
            budget.setAmount(details.getAmount());
            return ResponseEntity.ok(budgetSqlRepository.save(budget));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/sql/{id}")
    public ResponseEntity<Void> deleteBudgetSQL(@PathVariable Long id) {
        if (budgetSqlRepository.existsById(id)) {
            budgetSqlRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // CLOUD FIRESTORE PATHS (NoSQL)
    // ==========================================

//    @PostMapping("/firestore/{id}")
//    public ResponseEntity<String> createBudgetNoSQL(@PathVariable String id, @RequestBody BudgetDoc budget) throws Exception {
//        firestore.collection("budgets").document(id).set(budget).get();
//        return ResponseEntity.ok("Budget entry posted to Firestore");
//    }
//
//    @GetMapping("/firestore/{id}")
//    public ResponseEntity<BudgetDoc> getBudgetNoSQL(@PathVariable String id) throws Exception {
//        var doc = firestore.collection("budgets").document(id).get().get();
//        return doc.exists() ? ResponseEntity.ok(doc.toObject(BudgetDoc.class)) : ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/firestore/{id}")
//    public ResponseEntity<String> updateBudgetNoSQL(@PathVariable String id, @RequestBody BudgetDoc budget) throws Exception {
//        firestore.collection("budgets").document(id).set(budget, SetOptions.merge()).get();
//        return ResponseEntity.ok("Budget document merged in Firestore");
//    }
//
//    @DeleteMapping("/firestore/{id}")
//    public ResponseEntity<String> deleteBudgetNoSQL(@PathVariable String id) throws Exception {
//        firestore.collection("budgets").document(id).delete().get();
//        return ResponseEntity.ok("Budget record deleted from Firestore");
//    }
}