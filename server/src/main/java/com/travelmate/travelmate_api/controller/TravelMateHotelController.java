package com.travelmate.travelmate_api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateHotelController {

    @Value("${google.places.api.key}")
    private String googlePlacesApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<?> getHotels(@RequestParam String location) {
        String url = "https://places.googleapis.com/v1/places:searchText";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("textQuery", "hotels in " + location);
        requestBody.put("includedType", "lodging");
        requestBody.put("maxResultCount", 8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", googlePlacesApiKey);
        headers.set(
                "X-Goog-FieldMask",
                "places.id,places.displayName,places.formattedAddress,places.rating,places.googleMapsUri");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> googleResponse = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        List<Map<String, Object>> hotels = new ArrayList<>();

        Map<String, Object> responseBody = googleResponse.getBody();

        if (responseBody == null) {
            return ResponseEntity.ok(hotels);
        }

        List<Map<String, Object>> places = (List<Map<String, Object>>) responseBody.get("places");

        if (places != null) {
            for (Map<String, Object> place : places) {
                Map<String, Object> hotel = new HashMap<>();

                Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");

                hotel.put("id", place.get("id"));
                hotel.put("name", displayName != null ? displayName.get("text") : "Unknown hotel");
                hotel.put("address", place.get("formattedAddress"));
                hotel.put("rating", place.getOrDefault("rating", "No rating"));
                hotel.put("mapsUrl", place.get("googleMapsUri"));

                hotels.add(hotel);
            }
        }

        return ResponseEntity.ok(hotels);
    }
}