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

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // ==========================================
        // 1. DYNAMIC SEEDING: DESTINATIONS
        // ==========================================
        if (destinationRepository.count() == 0) {
            System.out.println("📂 Reading destinations.json catalog source...");
            InputStream destStream = resourceLoader.getResource("classpath:data/destinations.json").getInputStream();
            
            List<DestinationSQL> externalDestinations = mapper.readValue(destStream, new TypeReference<List<DestinationSQL>>(){});
            
            for (DestinationSQL dest : externalDestinations) {
                DestinationSQL savedSql = destinationRepository.save(dest);
                
                DestinationDoc destDoc = new DestinationDoc();
                destDoc.setId("DEST-" + savedSql.getId());
                destDoc.setName(savedSql.getName());
                destDoc.setOverview(savedSql.getOverview());
                destDoc.setImage(savedSql.getImage());
                destDoc.setActivities(savedSql.getActivities());
                
                firestore.collection("destinations").document(destDoc.getId()).set(destDoc).get();
            }
            System.out.println("✅ Symmetrically synchronized global destinations catalog items.");
        }

        // ==========================================
        // 2. DYNAMIC SEEDING: TRIPS (MODIFIED FOR PLURAL SCHEMA)
        // ==========================================
        if (tripRepository.count() == 0) {
            System.out.println("📂 Reading trips.json scheduling source...");
            InputStream tripStream = resourceLoader.getResource("classpath:data/trips.json").getInputStream();
            
            List<TripDoc> externalTrips = mapper.readValue(tripStream, new TypeReference<List<TripDoc>>(){});
            
            for (TripDoc doc : externalTrips) {
            	destinationRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(doc.getDestinationName()))
                .findFirst()
                .ifPresent(matchedDest -> {
                    TripsSQL tripSql = new TripsSQL();
                    tripSql.setStartDate(LocalDate.parse(doc.getStartDate()));
                    tripSql.setEndDate(LocalDate.parse(doc.getEndDate()));
                    tripSql.setMaxTravelers(doc.getMaxTravelers());
                    
                    // 🚀 THE FIX FOR SEEDER: Save the parent trip first to get an database ID generated
                    TripsSQL savedTrip = tripRepository.save(tripSql);
                    
                    // Now link the child to the saved parent record row
                    matchedDest.setTrip(savedTrip);
                    destinationRepository.save(matchedDest); // Update the child entity safely!
                });

                firestore.collection("trips").document(doc.getTripId()).set(doc).get();
            }
            System.out.println("✅ Symmetrically synchronized dynamic user trip records.");
        }

        System.out.println("🎉 Multi-cloud system ingestion complete without hardcoded variables.");
    }
}