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
    public void register(User user) {

    // convert role to uppercase (IMPORTANT for your DB match)
    user.setRole(user.getRole().toUpperCase());

    // optional: basic validation
    if (userRepository.findByEmailAndRole(user.getEmail(), user.getRole()).isPresent()) {
        throw new RuntimeException("User already exists");
    }

    userRepository.save(user);

    System.out.println("✅ USER REGISTERED");
}

    public User login(String email, String password, String role) {

    System.out.println("EMAIL: " + email);
    role = role.toUpperCase();
    System.out.println("ROLE: " + role);

    Optional<User> userOpt = userRepository.findByEmailAndRole(email, role);

    if (userOpt.isEmpty()) {
        System.out.println("❌ USER NOT FOUND");
        throw new RuntimeException("User not found");
    }

    User user = userOpt.get();

    System.out.println("DB PASSWORD: " + user.getPassword());
    System.out.println("ENTERED PASSWORD: " + password);

    if (!user.getPassword().equals(password)) {
        System.out.println("❌ PASSWORD WRONG");
        throw new RuntimeException("Invalid password");
    }

    System.out.println("✅ LOGIN SUCCESS");

    return user;
}
}
