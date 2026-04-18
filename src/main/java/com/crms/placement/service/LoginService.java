package com.crms.placement.service;

import com.crms.placement.model.User;
import com.crms.placement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;


@Service
public class LoginService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // REGISTER
    public User register(User user) {

        if (user.getRole() == null) {
            throw new RuntimeException("Role cannot be null");
        }

        user.setRole(user.getRole().toUpperCase());

        System.out.println("🔍 [REGISTER] Checking for existing user: " + user.getEmail() + " | Role: " + user.getRole());

        // safety check
        if (userRepository.findByEmailAndRole(user.getEmail(), user.getRole()).isPresent()) {
            throw new RuntimeException("User already exists with this email and role");
        }

        // ✅ HASH PASSWORD BEFORE SAVING
        String plainPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(plainPassword);
        user.setPassword(hashedPassword);

        System.out.println("🔒 [REGISTER] Password hashed successfully");

        User savedUser = userRepository.save(user);

        System.out.println("✅ [REGISTER] USER SAVED: ID=" + savedUser.getUserId() 
            + " | Email=" + savedUser.getEmail() 
            + " | Role=" + savedUser.getRole());

        return savedUser;
    }

    // LOGIN
    public User login(String email, String password, String role) {

        if (role == null || role.trim().isEmpty()) {
            throw new RuntimeException("Role cannot be null or empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email cannot be null or empty");
        }

        role = role.toUpperCase();

        System.out.println("🔍 [LOGIN] Attempting login for: " + email + " | Role: " + role);

        Optional<User> userOpt = userRepository.findByEmailAndRole(email, role);

        if (userOpt.isEmpty()) {
            System.out.println("❌ [LOGIN] USER NOT FOUND - Email: " + email + " | Role: " + role);
            throw new RuntimeException("User not found with email: " + email + " and role: " + role);
        }

        User user = userOpt.get();

        System.out.println("✅ [LOGIN] USER FOUND: ID=" + user.getUserId() 
            + " | Name=" + user.getName() 
            + " | Role=" + user.getRole());

        // ✅ VERIFY HASHED PASSWORD
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

        if (!passwordMatches) {
            System.out.println("❌ [LOGIN] WRONG PASSWORD for user: " + user.getEmail());
            throw new RuntimeException("Invalid password");
        }

        System.out.println("✅ [LOGIN] PASSWORD CORRECT - Login successful for: " + user.getEmail());

        return user;
    }
}