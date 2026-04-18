package com.crms.placement.controller;

import com.crms.placement.model.Admin;
import com.crms.placement.model.User;
import com.crms.placement.repository.AdminRepository;
import com.crms.placement.service.AdminDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final AdminRepository adminRepository;
    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminRepository adminRepository,
                                    AdminDashboardService dashboardService) {
        this.adminRepository = adminRepository;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // ── 1. Get logged-in user from session ────────────────────
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            System.out.println("❌ [Admin Dashboard] No loggedInUser in session → redirect to login");
            return "redirect:/login";
        }
        model.addAttribute("activePage", "Dashboard");
        model.addAttribute("user", user);
        model.addAttribute("username", user.getName());

        System.out.println("✅ [Admin Dashboard] loggedInUser: " + user.getEmail()
                + " | userId: " + user.getUserId());

        return "pages/admin_dashboard";
    }
}