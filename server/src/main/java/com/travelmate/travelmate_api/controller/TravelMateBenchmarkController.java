package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.models.nosql.UserDoc;
import com.travelmate.travelmate_api.models.sql.UserSQL;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;

@RestController
@RequestMapping("/api/benchmarks")
@CrossOrigin(origins = "http://localhost:5173/")
public class TravelMateBenchmarkController {
	@Autowired
    private UserSQLRepository userSqlRepository;

    @Autowired
    private Firestore firestore;

    @GetMapping("/latest-run")
    public Map<String, Object> runPerformanceTest() throws Exception {
        String testEmail = "benchmark_test_" + System.currentTimeMillis() + "@travelmate.com";
        String testName = "Benchmark Runner";

        // ==========================================
        // 1. BENCHMARK GOOGLE CLOUD SQL (MySQL)
        // ==========================================
        long sqlStart = System.nanoTime();
        
        // Execute a live write followed by a read check
        UserSQL sqlUser = userSqlRepository.save(new UserSQL(testName, testEmail));
        userSqlRepository.findById(sqlUser.getId());
        
        long sqlEnd = System.nanoTime();
        double sqlDurationMs = (sqlEnd - sqlStart) / 1_000_000.0;

        // ==========================================
        // 2. BENCHMARK GOOGLE CLOUD FIRESTORE (NoSQL)
        // ==========================================
        long firestoreStart = System.nanoTime();
        String firestoreId = "BENCH-" + sqlUser.getId();

        UserDoc noSqlUser = new UserDoc();
        noSqlUser.setUserId(firestoreId);
        noSqlUser.setName(testName);
        noSqlUser.setEmail(testEmail);
        noSqlUser.setEmbeddedBookings(new ArrayList<>());

        // Execute a live write followed by a read block check
        firestore.collection("users").document(firestoreId).set(noSqlUser).get();
        firestore.collection("users").document(firestoreId).get().get();
        
        long firestoreEnd = System.nanoTime();
        double firestoreDurationMs = (firestoreEnd - firestoreStart) / 1_000_000.0;

        // Clean up test records immediately so we don't bloat your databases
        userSqlRepository.delete(sqlUser);
        firestore.collection("users").document(firestoreId).delete();

        // ==========================================
        // 3. DETERMINE THE METRIC WINNER
        // ==========================================
        String winner = (firestoreDurationMs < sqlDurationMs) ? "Cloud Firestore" : "Cloud SQL";
        double margin = Math.abs(sqlDurationMs - firestoreDurationMs);

        // Map and return data payloads back to Axios
        Map<String, Object> metrics = new HashMap<>();
        metrics.putAll(Map.of(
            "firestoreTime", String.format("%.2f ms", firestoreDurationMs),
            "sqlTime", String.format("%.2f ms", sqlDurationMs),
            "winner", winner,
            "margin", String.format("%.2f ms", margin)
        ));
        
        return metrics;
    }
}
