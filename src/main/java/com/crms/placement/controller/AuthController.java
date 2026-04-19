package com.crms.placement.controller;

import com.crms.placement.dto.RegistrationRequestDTO;
import com.crms.placement.model.User;
import com.crms.placement.service.LoginService;
import com.crms.placement.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final LoginService loginService;
    private final RegistrationService registrationService;

    public AuthController(LoginService loginService, RegistrationService registrationService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
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
            HttpSession session,
            Model model
    ) {
        try {
            User user = loginService.login(email, password, role);

            // store in session (important)
            session.setAttribute("user", user);

            // Role based redirection
            String redirectUrl = "/job-board"; // default
            if ("HR".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "/hr/dashboard";
            } else if ("ALUMNI".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "/alumni/dashboard";
            } else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "/admin/dashboard";
            }

            return "redirect:" + redirectUrl;

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pages/login";
        }
    }

    // show register page
    @GetMapping("/register")
    public String showRegister() {
        return "pages/register";
    }

    // handle registration
    @PostMapping("/register")
    public String register(RegistrationRequestDTO request, HttpSession session, Model model) {
        try {
            User user = registrationService.register(request);
            session.setAttribute("user", user);
            
            // Redirection after registration
            String redirectUrl = "/job-board";
            if ("HR".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "/hr/dashboard";
            } else if ("ALUMNI".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "/alumni/dashboard";
            }
            
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pages/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}