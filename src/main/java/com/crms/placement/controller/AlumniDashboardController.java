package com.crms.placement.controller;

import com.crms.placement.model.AlumniUser;
import com.crms.placement.model.User;
import com.crms.placement.repository.AlumniUserRepository;
import com.crms.placement.service.AlumniDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlumniDashboardController {

    private final AlumniUserRepository alumniUserRepository;
    private final AlumniDashboardService dashboardService;

    public AlumniDashboardController(AlumniUserRepository alumniUserRepository,
                                     AlumniDashboardService dashboardService) {
        this.alumniUserRepository = alumniUserRepository;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/alumni/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // ── 1. Get logged-in user from session ────────────────────
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("❌ [Alumni Dashboard] No loggedInUser in session → redirect to login");
            return "redirect:/login";
        }

        model.addAttribute("activePage", "Dashboard");
        model.addAttribute("user", user);
        model.addAttribute("username", user.getName());

        System.out.println("✅ [Alumni Dashboard] loggedInUser: " + user.getEmail()
                + " | userId: " + user.getUserId());

        // ── 2. Fetch AlumniUser by userId (same PK via @MapsId) ──────
        AlumniUser alumniUser = alumniUserRepository.findById(user.getUserId()).orElse(null);

        if (alumniUser == null) {
            System.out.println("❌ [Alumni Dashboard] No AlumniUser row found for userId="
                    + user.getUserId());
            
            // ✅ ADD DEFAULT VALUES TO PREVENT NULL ERRORS
            model.addAttribute("alumni", null);
            model.addAttribute("totalCareerHistory", 0L);
            model.addAttribute("totalOAPrep", 0L);
            model.addAttribute("currentCompany", "Not Set");
            
            return "pages/alumni_dashboard";
        }
        
        System.out.println("✅ [Alumni Dashboard] AlumniUser found: " + alumniUser.getName());

        // ── 3. Load alumni stats ──────────────────────────────────
        Long alumniId = alumniUser.getId();

        long totalCareerHistory = dashboardService.getTotalCareerHistory(alumniId);
        long totalOAPrep = dashboardService.getTotalOAPrep(alumniId);

        // ── 4. Pass to template ───────────────────────────────────
        model.addAttribute("alumni", alumniUser);
        model.addAttribute("totalCareerHistory", totalCareerHistory);
        model.addAttribute("totalOAPrep", totalOAPrep);
        model.addAttribute("currentCompany", 
            alumniUser.getCurrentCompany() != null ? alumniUser.getCurrentCompany() : "Not Set");

        return "pages/alumni_dashboard";
    }
}