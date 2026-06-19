package com.travelmate.travelmate_api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = {"http://localhost:5173"})
public class TravelMatePlaceController {

    @Value("${google.places.api.key}")
    private String googlePlacesApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/search")
    public ResponseEntity<?> searchPlaces(
            @RequestParam String location,
            @RequestParam String query
    ) {
        String url = "https://places.googleapis.com/v1/places:searchText";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("textQuery", query + " tourist attractions in " + location);
        requestBody.put("maxResultCount", 8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", googlePlacesApiKey);
        headers.set(
                "X-Goog-FieldMask",
                "places.id,places.displayName,places.formattedAddress,places.rating,places.googleMapsUri,places.types"
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> googleResponse =
                restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> responseBody = googleResponse.getBody();

        if (responseBody == null) {
            return ResponseEntity.ok(results);
        }

        List<Map<String, Object>> places =
                (List<Map<String, Object>>) responseBody.get("places");

        if (places != null) {
            for (Map<String, Object> place : places) {
                Map<String, Object> result = new HashMap<>();

                Map<String, Object> displayName =
                        (Map<String, Object>) place.get("displayName");

                result.put("id", place.get("id"));
                result.put("name", displayName != null ? displayName.get("text") : "Unknown place");
                result.put("address", place.get("formattedAddress"));
                result.put("rating", place.getOrDefault("rating", "No rating"));
                result.put("mapsUrl", place.get("googleMapsUri"));
                result.put("types", place.get("types"));

                results.add(result);
            }
        }

        return ResponseEntity.ok(results);
    }
}