package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.travelmate.travelmate_api.models.nosql.DestinationDoc;
import com.travelmate.travelmate_api.models.sql.DestinationSQL;
import com.travelmate.travelmate_api.repository.sql.DestinationSQLRepository;

@RestController
@RequestMapping("/api/travel/destinations")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateDestinationController {

    private final DestinationSQLRepository destinationSqlRepository;
    private final Firestore firestore;

    public TravelMateDestinationController(DestinationSQLRepository destinationSqlRepository, Firestore firestore) {
        this.destinationSqlRepository = destinationSqlRepository;
        this.firestore = firestore;
    }

    // --- CLOUD SQL PATH ---
    @GetMapping("/sql")
    public ResponseEntity<List<DestinationSQL>> getAllDestinationsSQL() {
        return ResponseEntity.ok(destinationSqlRepository.findAll());
    }

    // --- CLOUD FIRESTORE PATH ---
    @GetMapping("/firestore")
    public ResponseEntity<List<DestinationDoc>> getAllDestinationsNoSQL() throws Exception {
        var future = firestore.collection("destinations").get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<DestinationDoc> list = new ArrayList<>();
        for (var doc : docs) {
            list.add(doc.toObject(DestinationDoc.class));
        }
        return ResponseEntity.ok(list);
    }
}
