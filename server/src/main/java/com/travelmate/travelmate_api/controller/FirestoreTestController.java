package com.travelmate.travelmate_api.controller;

import com.google.cloud.firestore.Firestore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class FirestoreTestController {

    private final Firestore firestore;
    private final JdbcTemplate jdbcTemplate; // Built-in Spring helper for raw SQL

    // Spring Boot automatically injects both using only your application.properties
    public FirestoreTestController(Firestore firestore, JdbcTemplate jdbcTemplate) {
        this.firestore = firestore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test-databases")
    public Map<String, Object> testBothDatabases() {
        String mysqlStatus;
        String firestoreStatus;

        // 1. Test Cloud SQL Proxy Connection (No tables needed, just checking system time)
        try {
            Long mysqlTime = jdbcTemplate.queryForObject("SELECT UNIX_TIMESTAMP()", Long.class);
            mysqlStatus = "Connected! Cloud SQL Server Timestamp: " + mysqlTime;
        } catch (Exception e) {
            mysqlStatus = "Connection Failed: " + e.getMessage();
        }

        // 2. Test Firestore NoSQL Connection
        try {
            Map<String, Object> testData = Map.of(
                "status", "Live",
                "timestamp", System.currentTimeMillis()
            );
            // Pushes a document to check the gRPC credential handshake
            firestore.collection("connection_tests").document("ping").set(testData);
            firestoreStatus = "Connected! Document updated successfully in travelmate-nosql.";
        } catch (Exception e) {
            firestoreStatus = "Connection Failed: " + e.getMessage();
        }

        // Return a clean JSON response to your browser
        return Map.of(
            "cloud_sql_mysql", mysqlStatus,
            "cloud_firestore_nosql", firestoreStatus
        );
    }
}