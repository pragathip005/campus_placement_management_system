package com.crms.placement.controller;

import com.crms.placement.dto.AdminStatsDto;
import com.crms.placement.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminDashboardController {

    private final StatisticsService statisticsService;

    public AdminDashboardController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // Serves the Thymeleaf dashboard page at GET /admin/dashboard
    // No security config present yet — restrict to ROLE_ADMIN when SecurityConfig is added
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        AdminStatsDto stats = statisticsService.getAdminStats();
        model.addAttribute("stats", stats);
        return "pages/admin-dashboard";
    }

    // REST endpoint for Chart.js / JS to fetch live data as JSON
    @GetMapping("/api/admin/stats")
    @ResponseBody
    public AdminStatsDto getStats() {
        return statisticsService.getAdminStats();
    }
}
