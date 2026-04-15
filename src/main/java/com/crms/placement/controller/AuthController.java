package com.crms.placement.controller;

import com.crms.placement.model.User;
import com.crms.placement.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    // show login page
    @GetMapping("/login")
    public String showLogin() {
        return "pages/login";
    }

    // handle login
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model
    ) {
        try {
            User user = loginService.login(email, password, role);

            // store in session (important)
            model.addAttribute("user", user);

            return "redirect:/job-board"; // or role-based redirect

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pages/login";
        }
    }
}