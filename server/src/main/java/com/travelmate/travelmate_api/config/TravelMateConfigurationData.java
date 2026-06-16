package com.travelmate.travelmate_api.config;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.firestore.Firestore;

import com.travelmate.travelmate_api.models.nosql.DestinationDoc;
import com.travelmate.travelmate_api.models.nosql.TripDoc;
import com.travelmate.travelmate_api.models.sql.DestinationSQL;
import com.travelmate.travelmate_api.models.sql.TripsSQL;
import com.travelmate.travelmate_api.repository.sql.DestinationSQLRepository;
import com.travelmate.travelmate_api.repository.sql.TripSQLRepository;

@Component
public class TravelMateConfigurationData implements CommandLineRunner {
	
    private final DestinationSQLRepository destinationRepository;
    private final TripSQLRepository tripRepository;
    private final Firestore firestore;
    private final ResourceLoader resourceLoader;

    public TravelMateConfigurationData(DestinationSQLRepository destinationRepository,
                                       TripSQLRepository tripRepository,
                                       Firestore firestore,
                                       ResourceLoader resourceLoader) {
        this.destinationRepository = destinationRepository;
        this.tripRepository = tripRepository;
        this.firestore = firestore;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Initializing Dynamic Multi-Cloud JSON Seeding Engine...");

        // Instantiate Jackson ObjectMapper with Java 8 Time capabilities (for LocalDate mappings)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // ==========================================
        // 1. DYNAMIC SEEDING: DESTINATIONS
        // ==========================================
        if (destinationRepository.count() == 0) {
            System.out.println("📂 Reading destinations.json catalog source...");
            InputStream destStream = resourceLoader.getResource("classpath:data/destinations.json").getInputStream();
            
            // Parse directly from file streams into object list representations
            List<DestinationSQL> externalDestinations = mapper.readValue(destStream, new TypeReference<List<DestinationSQL>>(){});
            
            for (DestinationSQL dest : externalDestinations) {
                // Persistent save to Cloud SQL
                DestinationSQL savedSql = destinationRepository.save(dest);
                
                // Construct mirror replica for Firestore NoSQL collection mapping
                DestinationDoc destDoc = new DestinationDoc();
                destDoc.setId("DEST-" + savedSql.getId());
                destDoc.setName(savedSql.getName());
                destDoc.setOverview(savedSql.getOverview());
                destDoc.setImage(savedSql.getImage());
                destDoc.setActivities(savedSql.getActivities());
                
                // Write to Firestore asynchronously
                firestore.collection("destinations").document(destDoc.getId()).set(destDoc).get();
            }
            System.out.println("✅ Symmetrically synchronized global destinations catalog items.");
        }

        // ==========================================
        // 2. DYNAMIC SEEDING: TRIPS
        // ==========================================
        if (tripRepository.count() == 0) {
            System.out.println("📂 Reading trips.json scheduling source...");
            InputStream tripStream = resourceLoader.getResource("classpath:data/trips.json").getInputStream();
            
            // Map JSON array records onto loose data transfer templates
            List<TripDoc> externalTrips = mapper.readValue(tripStream, new TypeReference<List<TripDoc>>(){});
            
            for (TripDoc doc : externalTrips) {
                // Query database dynamically to link existing foreign-key constraints safely
                destinationRepository.findAll().stream()
                    .filter(d -> d.getName().equalsIgnoreCase(doc.getDestinationName()))
                    .findFirst()
                    .ifPresent(matchedDest -> {
                        TripsSQL tripSql = new TripsSQL();
                        tripSql.setDestination(matchedDest);
                        tripSql.setStartDate(LocalDate.parse(doc.getStartDate()));
                        tripSql.setEndDate(LocalDate.parse(doc.getEndDate()));
                        tripSql.setMaxTravelers(doc.getMaxTravelers());
                        
                        // Save relation inside Cloud SQL
                        tripRepository.save(tripSql);
                    });

                // Save configuration entry to Firestore collection paths
                firestore.collection("trips").document(doc.getTripId()).set(doc).get();
            }
            System.out.println("✅ Symmetrically synchronized dynamic user trip records.");
        }

        System.out.println("🎉 Multi-cloud system ingestion complete without hardcoded variables.");
    }
}