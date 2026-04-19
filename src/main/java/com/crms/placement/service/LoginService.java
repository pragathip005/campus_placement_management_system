package com.crms.placement.service;

import com.crms.placement.model.User;
import com.crms.placement.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // REGISTER - PLAIN TEXT PASSWORD
    public User register(User user) {

        if (user.getRole() == null) {
            throw new RuntimeException("Role cannot be null");
        }

        user.setRole(user.getRole().toUpperCase());

        System.out.println("🔍 [REGISTER] Checking for existing user: " + user.getEmail() + " | Role: " + user.getRole());

        // Check if user already exists
        if (userRepository.findByEmailAndRole(user.getEmail(), user.getRole()).isPresent()) {
            throw new RuntimeException("User already exists with this email and role");
        }

        // ✅ PASSWORD STORED AS PLAIN TEXT - NO HASHING
        System.out.println("🔓 [REGISTER] Storing password as plain text: " + user.getPassword());

        User savedUser = userRepository.save(user);

        System.out.println("✅ [REGISTER] USER SAVED: ID=" + savedUser.getUserId() 
            + " | Email=" + savedUser.getEmail() 
            + " | Role=" + savedUser.getRole()
            + " | Password=" + savedUser.getPassword());

        return savedUser;
    }

    // LOGIN - PLAIN TEXT STRING COMPARISON
    public User login(String email, String password, String role) {

        if (role == null || role.trim().isEmpty()) {
            throw new RuntimeException("Role cannot be null or empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email cannot be null or empty");
        }

        role = role.toUpperCase();

        System.out.println("🔍 [LOGIN] Attempting login for: " + email + " | Role: " + role + " | Password: " + password);

        Optional<User> userOpt = userRepository.findByEmailAndRole(email, role);

        if (userOpt.isEmpty()) {
            System.out.println("❌ [LOGIN] USER NOT FOUND - Email: " + email + " | Role: " + role);
            throw new RuntimeException("User not found with email: " + email + " and role: " + role);
        }

        User user = userOpt.get();

        System.out.println("✅ [LOGIN] USER FOUND: ID=" + user.getUserId() 
            + " | Name=" + user.getName() 
            + " | Role=" + user.getRole()
            + " | Stored Password=" + user.getPassword());

        // ✅ SIMPLE PLAIN TEXT STRING COMPARISON
        System.out.println("🔓 [LOGIN] Comparing passwords: Input='" + password + "' | Stored='" + user.getPassword() + "'");
        
        if (!password.equals(user.getPassword())) {
            System.out.println("❌ [LOGIN] PASSWORD MISMATCH for user: " + user.getEmail());
            throw new RuntimeException("Invalid password");
        }

        System.out.println("✅ [LOGIN] PASSWORD MATCH - Login successful for: " + user.getEmail());

        return user;
    }
}