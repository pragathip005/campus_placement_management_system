package com.crms.placement.controller;

import com.crms.placement.model.User;
import com.crms.placement.service.LoginService;

import jakarta.servlet.http.HttpSession;

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
            Model model,
            HttpSession session
    ) {
        try {
            User user = loginService.login(email, password, role);
            session.setAttribute("user", user); // ✅ THIS is real login
            return "redirect:/job-board";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pages/login";
        }
    }
    @GetMapping("/register")
public String showRegister() {
    return "pages/register";
}

@PostMapping("/register")
public String register(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String role,
        @RequestParam(required = false) String branch,
        @RequestParam(required = false) Double cgpa,
        @RequestParam(required = false) Integer backlogCount,
        @RequestParam(required = false) String companyName,
        Model model
) {
    try {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        if (role.equals("student") || role.equals("alumni")) {
            user.setCgpa(cgpa);
            user.setBacklogCount(backlogCount);
        }

        // save using your service/repo
        loginService.register(user); // or userService.save(user)

        return "redirect:/login";

    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "pages/register";
    }
}
}