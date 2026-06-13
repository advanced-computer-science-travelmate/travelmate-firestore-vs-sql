package com.travelmate.travelmate_api.controller;

import com.travelmate.travelmate_api.models.sql.UserSQL;
//import com.travelmate.travelmate_api.models.firestore.UserDoc;
import com.travelmate.travelmate_api.repository.sql.UserSQLRepository;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // ==========================================
    // CLOUD SQL PATHS (Relational)
    // ==========================================

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

//    @PostMapping("/firestore")
//    public ResponseEntity<String> createUserNoSQL(@RequestBody UserDoc user) throws Exception {
//        firestore.collection("users").document(user.getUserId()).set(user).get();
//        return ResponseEntity.ok("User created in Firestore");
//    }
//
//    @GetMapping("/firestore/{id}")
//    public ResponseEntity<UserDoc> getUserNoSQL(@PathVariable String id) throws Exception {
//        var doc = firestore.collection("users").document(id).get().get();
//        return doc.exists() ? ResponseEntity.ok(doc.toObject(UserDoc.class)) : ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/firestore/{id}")
//    public ResponseEntity<String> updateUserNoSQL(@PathVariable String id, @RequestBody UserDoc user) throws Exception {
//        firestore.collection("users").document(id).set(user, SetOptions.merge()).get();
//        return ResponseEntity.ok("User updated in Firestore via merge options");
//    }
//
//    @DeleteMapping("/firestore/{id}")
//    public ResponseEntity<String> deleteUserNoSQL(@PathVariable String id) throws Exception {
//        firestore.collection("users").document(id).delete().get();
//        return ResponseEntity.ok("User document purged from Firestore");
//    }
}