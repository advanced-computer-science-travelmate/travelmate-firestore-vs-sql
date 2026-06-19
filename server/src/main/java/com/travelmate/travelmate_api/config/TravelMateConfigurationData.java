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
    // 1. ALWAYS READ DESTINATIONS JSON
    // ==========================================
    System.out.println("📂 Reading destinations.json catalog source...");
    InputStream destStream = resourceLoader.getResource("classpath:data/destinations.json").getInputStream();

    List<DestinationSQL> externalDestinations =
            mapper.readValue(destStream, new TypeReference<List<DestinationSQL>>() {});

    boolean shouldSeedDestinationSql = destinationRepository.count() == 0;

    for (DestinationSQL dest : externalDestinations) {

        // Save to SQL only if SQL table is empty
        if (shouldSeedDestinationSql) {
            destinationRepository.save(dest);
        }

        // Always seed/update Firestore
        DestinationDoc destDoc = new DestinationDoc();
        destDoc.setId("DEST-" + dest.getName().toUpperCase().replace(" ", "-"));
        destDoc.setName(dest.getName());
        destDoc.setOverview(dest.getOverview());
        destDoc.setImage(dest.getImage());
        destDoc.setActivities(dest.getActivities());

        firestore.collection("destinations")
                .document(destDoc.getId())
                .set(destDoc)
                .get();
    }

    System.out.println("✅ Firestore destinations seeded/updated: " + externalDestinations.size());

    // ==========================================
    // 2. ALWAYS READ TRIPS JSON
    // ==========================================
    System.out.println("📂 Reading trips.json scheduling source...");
    InputStream tripStream = resourceLoader.getResource("classpath:data/trips.json").getInputStream();

    List<TripDoc> externalTrips =
            mapper.readValue(tripStream, new TypeReference<List<TripDoc>>() {});

    boolean shouldSeedTripSql = tripRepository.count() == 0;

    for (TripDoc doc : externalTrips) {

        // Save to SQL only if SQL trip table is empty
        if (shouldSeedTripSql) {
            destinationRepository.findAll().stream()
                    .filter(d -> d.getName().equalsIgnoreCase(doc.getDestinationName()))
                    .findFirst()
                    .ifPresent(matchedDest -> {
                        TripsSQL tripSql = new TripsSQL();
                        tripSql.setStartDate(LocalDate.parse(doc.getStartDate()));
                        tripSql.setEndDate(LocalDate.parse(doc.getEndDate()));
                        tripSql.setMaxTravelers(doc.getMaxTravelers());

                        TripsSQL savedTrip = tripRepository.save(tripSql);

                        matchedDest.setTrip(savedTrip);
                        destinationRepository.save(matchedDest);
                    });
        }

        // Always seed/update Firestore
        firestore.collection("trips")
                .document(doc.getTripId())
                .set(doc)
                .get();
    }

    System.out.println("✅ Firestore trips seeded/updated: " + externalTrips.size());

    System.out.println("🎉 Multi-cloud system ingestion complete.");
}}