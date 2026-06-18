package com.travelmate.travelmate_api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.travelmate.travelmate_api.models.nosql.UserDoc;
import com.travelmate.travelmate_api.models.sql.UserSQL;
//import com.travelmate.travelmate_api.models.firestore.UserDoc;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelMateUserController {

    private final UserSQLRepository userSqlRepository;
    private final Firestore firestore;

    public TravelMateUserController(UserSQLRepository userSqlRepository, Firestore firestore) {
        this.userSqlRepository = userSqlRepository;
        this.firestore = firestore;
    }
    
    @PostMapping("/mock-login")
    public Map<String, String> handleDynamicLogin(@RequestBody Map<String, String> loginRequest) throws Exception {
        String emailAddress = loginRequest.get("email");
        String fullName = loginRequest.get("name");

        // 🚀 THE FIX: If name is null or missing (like on a direct login step), 
        // dynamically extract the prefix from the email address as a fallback name!
        if (fullName == null || fullName.trim().isEmpty()) {
            if (emailAddress != null && emailAddress.contains("@")) {
                fullName = emailAddress.split("@")[0];
            } else {
                fullName = "Active User";
            }
        }

        final String finalName = fullName; // Variable required for the stream closure below

        // Structural Generation or Matching for Google Cloud SQL
        UserSQL sqlUser = userSqlRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailAddress))
                .findFirst()
                .orElseGet(() -> userSqlRepository.save(new UserSQL(finalName, emailAddress)));

        // Generate matching String ID for Firestore NoSQL Collection Document
        String firestoreId = "USER-" + sqlUser.getId();

        UserDoc noSqlUser = new UserDoc();
        noSqlUser.setUserId(firestoreId);
        noSqlUser.setName(sqlUser.getName());
        noSqlUser.setEmail(emailAddress);
        noSqlUser.setEmbeddedBookings(new ArrayList<>());
        
        // Push payload to Cloud Firestore
        firestore.collection("users").document(firestoreId).set(noSqlUser).get();

        // Return both synchronized keys back to Axios session state management
        return Map.of(
            "sqlId", String.valueOf(sqlUser.getId()),
            "noSqlId", firestoreId,
            "name", sqlUser.getName(),
            "email", emailAddress
        );
    }
    
    @PostMapping("/sql")
    public ResponseEntity<UserSQL> createUserSQL(@RequestBody UserSQL user) {
        return ResponseEntity.ok(userSqlRepository.save(user));
    }

    @GetMapping("/sql")
    public ResponseEntity<List<UserSQL>> getAllUsersSQL() {
        return ResponseEntity.ok(userSqlRepository.findAll());
    }

    @PutMapping("/sql/{id}")
    public ResponseEntity<UserSQL> updateUserSQL(@PathVariable Long id, @RequestBody UserSQL userDetails) {
        return userSqlRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            return ResponseEntity.ok(userSqlRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/sql/{id}")
    public ResponseEntity<Void> deleteUserSQL(@PathVariable Long id) {
        if (userSqlRepository.existsById(id)) {
            userSqlRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // CLOUD FIRESTORE PATHS (NoSQL)
    // ==========================================

    @PostMapping("/firestore")
    public ResponseEntity<String> createUserNoSQL(@RequestBody UserDoc user) throws Exception {
        firestore.collection("users").document(user.getUserId()).set(user).get();
        return ResponseEntity.ok("User created in Firestore");
    }

    @GetMapping("/firestore/{id}")
    public ResponseEntity<UserDoc> getUserNoSQL(@PathVariable String id) throws Exception {
        var doc = firestore.collection("users").document(id).get().get();
        return doc.exists() ? ResponseEntity.ok(doc.toObject(UserDoc.class)) : ResponseEntity.notFound().build();
    }

    @PutMapping("/firestore/{id}")
    public ResponseEntity<String> updateUserNoSQL(@PathVariable String id, @RequestBody UserDoc user) throws Exception {
        firestore.collection("users").document(id).set(user, SetOptions.merge()).get();
        return ResponseEntity.ok("User updated in Firestore via merge options");
    }

    @DeleteMapping("/firestore/{id}")
    public ResponseEntity<String> deleteUserNoSQL(@PathVariable String id) throws Exception {
        firestore.collection("users").document(id).delete().get();
        return ResponseEntity.ok("User document purged from Firestore");
    }
}