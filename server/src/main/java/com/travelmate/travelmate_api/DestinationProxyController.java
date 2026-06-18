package com.travelmate.travelmate_api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class DestinationProxyController {

    @GetMapping("/api/destinations/europe")
    public Object getEuropeanDestinations() {
        String url = "https://api.restcountries.com/countries/v5?region=Europe";

        HttpHeaders headers = new HttpHeaders();
       headers.setBearerAuth("rc_live_b57775a1ba684df3a223da884920b6c8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Object.class
        );

        return response.getBody();
    }
}