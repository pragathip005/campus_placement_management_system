package com.crms.placement.controller;

import com.crms.placement.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
public String getUsers(Model model) {
    System.out.println("🔥 USERS CONTROLLER HIT");
    model.addAttribute("users", userRepository.findAll());
    return "pages/users";
}
}