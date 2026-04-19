package com.crms.placement.controller;

import com.crms.placement.dto.AdminStatsDto;
import com.crms.placement.model.User;
import com.crms.placement.service.AdminDashboardService;
import com.crms.placement.service.StatisticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;
    private final StatisticsService statisticsService;

    public AdminDashboardController(AdminDashboardService dashboardService,
                                    StatisticsService statisticsService) {
        this.dashboardService = dashboardService;
        this.statisticsService = statisticsService;
    }

    // ================= DASHBOARD PAGE =================
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // ✅ Session check
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("❌ [Admin Dashboard] No user in session → redirect to login");
            return "redirect:/login";
        }

        model.addAttribute("activePage", "Dashboard");
        model.addAttribute("user", user);
        model.addAttribute("username", user.getName());

        System.out.println("✅ [Admin Dashboard] " + user.getEmail());

        // ✅ Add stats to page
        AdminStatsDto stats = statisticsService.getAdminStats();
        model.addAttribute("stats", stats);

        return "pages/admin-dashboard"; // make sure filename matches
    }

    // ================= API (for charts) =================
    @GetMapping("/api/admin/stats")
    @ResponseBody
    public AdminStatsDto getStats() {
        return statisticsService.getAdminStats();
    }
}