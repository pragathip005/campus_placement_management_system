package com.crms.placement.controller;

import com.crms.placement.model.*;
import com.crms.placement.repository.*;
import com.crms.placement.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class AuthController {

    private final LoginService loginService;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final AlumniUserRepository alumniUserRepository;
    private final AdminRepository adminRepository;

    public AuthController(LoginService loginService,
                          StudentRepository studentRepository,
                          CompanyRepository companyRepository,
                          AlumniUserRepository alumniUserRepository,
                          AdminRepository adminRepository) {
        this.loginService = loginService;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.alumniUserRepository = alumniUserRepository;
        this.adminRepository = adminRepository;
    }

    // ================= LOGIN =================

    @GetMapping("/login")
public String showLogin() {
    return "pages/login";
}

@PostMapping("/login")
public String login(@RequestParam String email,
                    @RequestParam String password,
                    @RequestParam String role,
                    HttpSession session,
                    Model model) {

    try {
        User user = loginService.login(email, password, role);

        // store user in session
        session.setAttribute("user", user);

        return switch (user.getRole().toUpperCase()) {
            case "HR" -> "redirect:/hr/dashboard";
            case "ALUMNI" -> "redirect:/alumni/dashboard";
            case "ADMIN" -> "redirect:/admin/dashboard";
            default -> "redirect:/student/dashboard";
        };

    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "pages/login";
    }
}

    // ================= REGISTER =================

    @GetMapping("/register")
public String showRegister() {
    return "pages/register";
}

@PostMapping("/register")
public String register(@RequestParam String name,
                       @RequestParam String email,
                       @RequestParam String password,
                       @RequestParam String role,
                       HttpSession session,
                       Model model) {

    try {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // plain or hashed inside service
        user.setRole(role.toUpperCase());

        User savedUser = loginService.register(user);

        session.setAttribute("user", savedUser);

        return switch (savedUser.getRole()) {
            case "HR" -> "redirect:/hr/dashboard";
            case "ALUMNI" -> "redirect:/alumni/dashboard";
            case "ADMIN" -> "redirect:/admin/dashboard";
            default -> "redirect:/student/dashboard";
        };

    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "pages/register";
    }
}

    // ================= LOGOUT =================

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
}
}