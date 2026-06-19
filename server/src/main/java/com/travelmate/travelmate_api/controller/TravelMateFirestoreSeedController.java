package com.travelmate.travelmate_api.controller;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import com.travelmate.travelmate_api.models.nosql.DestinationDoc;
import com.travelmate.travelmate_api.models.nosql.TripDoc;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class TravelMateFirestoreSeedController {

    private final Firestore firestore;
    private final ResourceLoader resourceLoader;

    public TravelMateFirestoreSeedController(
            Firestore firestore,
            ResourceLoader resourceLoader
    ) {
        this.firestore = firestore;
        this.resourceLoader = resourceLoader;
    }

    @PostMapping("/seed-firestore")
    public ResponseEntity<?> seedFirestore() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        InputStream destinationStream =
                resourceLoader.getResource("classpath:data/destinations.json").getInputStream();

        List<DestinationDoc> destinations =
                mapper.readValue(destinationStream, new TypeReference<List<DestinationDoc>>() {});

        for (DestinationDoc destination : destinations) {
            String documentId = "DEST-" + destination.getName()
                    .toUpperCase()
                    .replace(" ", "-");

            destination.setId(documentId);

            firestore.collection("destinations")
                    .document(documentId)
                    .set(destination)
                    .get();
        }

        InputStream tripStream =
                resourceLoader.getResource("classpath:data/trips.json").getInputStream();

        List<TripDoc> trips =
                mapper.readValue(tripStream, new TypeReference<List<TripDoc>>() {});

        for (TripDoc trip : trips) {
            firestore.collection("trips")
                    .document(trip.getTripId())
                    .set(trip)
                    .get();
        }

        return ResponseEntity.ok(
                "Firestore seeded successfully: "
                        + destinations.size()
                        + " destinations and "
                        + trips.size()
                        + " trips."
        );
    }
}