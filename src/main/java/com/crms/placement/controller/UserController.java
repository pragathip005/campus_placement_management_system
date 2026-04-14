package com.crms.placement.controller;

import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final StudentRepository studentRepository;

    public UserController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/users")
public String getUsers(Model model) {
    System.out.println("🔥 USERS CONTROLLER HIT");
    model.addAttribute("users", studentRepository.findAll());
    return "pages/users";
}
}
