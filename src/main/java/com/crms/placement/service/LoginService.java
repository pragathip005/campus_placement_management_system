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

    // REGISTER
    public User register(User user) {

        if (user.getRole() == null) {
            throw new RuntimeException("Role cannot be null");
        }

        user.setRole(user.getRole().toUpperCase());

        // safety check
        if (userRepository.findByEmailAndRole(user.getEmail(), user.getRole()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User savedUser = userRepository.save(user);

        System.out.println("✅ USER REGISTERED: " + savedUser.getEmail());

        return savedUser;
    }

    // LOGIN
    public User login(String email, String password, String role) {

        if (role == null) {
            throw new RuntimeException("Role cannot be null");
        }

        role = role.toUpperCase();

        Optional<User> userOpt = userRepository.findByEmailAndRole(email, role);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}