package com.travelmate.travelmate_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelmate.travelmate_api.models.sql.User;
import com.travelmate.travelmate_api.repository.sql.UserRepository;

@RestController
@RequestMapping("/api/travel/users")
public class TravelMateUserController {
	private final UserRepository userRepository;

    public TravelMateUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@RequestBody User newUser) {
        
        	if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
                throw new RuntimeException("Email already registered!");
            }
            return userRepository.save(newUser);
        
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @PutMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User updatedData) {
        try {
            return userRepository.findById(id).map(existingUser -> {
                existingUser.setName(updatedData.getName());
                existingUser.setEmail(updatedData.getEmail());
                userRepository.save(existingUser);
                return "Success! User ID " + id + " has been updated.";
            }).orElse("Error: User ID " + id + " not found.");
        } catch (Exception e) {
            return "Update failed: " + e.getMessage();
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return "Error: User ID " + id + " does not exist.";
            }
            userRepository.deleteById(id);
            return "Success! User ID " + id + " was deleted from Cloud SQL.";
        } catch (Exception e) {
            return "Deletion failed: " + e.getMessage();
        }
    }
}
