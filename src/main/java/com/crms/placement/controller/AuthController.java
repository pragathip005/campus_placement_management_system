package com.crms.placement.controller;

import com.crms.placement.dto.RegistrationRequestDTO;
import com.crms.placement.model.*;
import com.crms.placement.repository.*;
import com.crms.placement.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.crms.placement.service.RegistrationService;

import java.util.UUID;

@Controller
public class AuthController {

    private final LoginService loginService;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final AlumniUserRepository alumniUserRepository;
    private final AdminRepository adminRepository;
    private final RegistrationService registrationService;

    public AuthController(LoginService loginService,
                          StudentRepository studentRepository,
                          CompanyRepository companyRepository,
                          AlumniUserRepository alumniUserRepository,
                          AdminRepository adminRepository,
                          RegistrationService registrationService) {
        this.loginService = loginService;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.alumniUserRepository = alumniUserRepository;
        this.adminRepository = adminRepository;
        this.registrationService = registrationService;

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
public String register(
        @ModelAttribute RegistrationRequestDTO request,
        @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile,
        HttpSession session,
        Model model
) {
    try {
        User user = registrationService.register(request, resumeFile);

        session.setAttribute("user", user);

        return switch (user.getRole().toUpperCase()) {
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