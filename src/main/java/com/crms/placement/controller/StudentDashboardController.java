package com.crms.placement.controller;

import com.crms.placement.dto.ApplicationDashboardDto;
import com.crms.placement.model.Student;
import com.crms.placement.model.User;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.StudentDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.crms.placement.model.ApplicationStatus;

import java.util.List;

@Controller
public class StudentDashboardController {

    private final StudentRepository       studentRepository;
    private final StudentDashboardService dashboardService;

    public StudentDashboardController(StudentRepository studentRepository,
                                      StudentDashboardService dashboardService) {
        this.studentRepository  = studentRepository;
        this.dashboardService   = dashboardService;
    }

    @GetMapping("/student/dashboard")
public String dashboard(HttpSession session, Model model) {

    // ── 1. Get logged-in user from session ────────────────────
    User user = (User) session.getAttribute("user");
    if (user == null) {
        System.out.println("❌ [Dashboard] No loggedInUser in session → redirect to login");
        return "redirect:/login";
    }

    model.addAttribute("activePage", "Dashboard");
    model.addAttribute("user", user);
    model.addAttribute("username", user.getName());

    System.out.println("✅ [Dashboard] loggedInUser: " + user.getEmail()
            + " | userId: " + user.getUserId());

    // ── 2. Fetch Student by userId (same PK via @MapsId) ──────
    Student student = studentRepository.findById(user.getUserId()).orElse(null);

    if (student == null) {
        System.out.println("❌ [Dashboard] No Student row found for userId="
                + user.getUserId());
        
        // ✅ ADD DEFAULT VALUES TO PREVENT NULL ERRORS
        model.addAttribute("student", null);
        model.addAttribute("placed", false);
        model.addAttribute("placedCompanyName", null);
        model.addAttribute("applications", List.of());  // Empty list
        model.addAttribute("totalApplications", 0L);
        model.addAttribute("shortlistedCount", 0L);
        model.addAttribute("oaPendingCount", 0L);
        
        return "pages/dashboard";  // ← Render with empty data instead of redirect
    }
    
    System.out.println("✅ [Dashboard] Student found: " + student.getName()
            + " | placed=" + student.getIsPlaced());

    // ── 3. Load applications ──────────────────────────────────
    Integer studentId = student.getStudentId().intValue();

    List<ApplicationDashboardDto> applications =
            dashboardService.getDashboardApplications(studentId);

    long totalApplications = dashboardService.getTotalApplications(studentId);
    long shortlistedCount  = dashboardService.getShortlistedCount(studentId);
    long oaPendingCount    = dashboardService.getOaPendingCount(studentId);

    // ── 4. Pass to template ───────────────────────────────────
    model.addAttribute("student", student);
    boolean placed = false;
    String placedCompanyName = null;
    ApplicationDashboardDto placedApp = applications.stream()
    .filter(app -> app.getStatus() == ApplicationStatus.OFFER_ACCEPTED)
    .findFirst()
    .orElse(null);
    if (placedApp != null) {
        placed = true;
        placedCompanyName = placedApp.getCompanyName();
        }
    model.addAttribute("placed", placed);
    model.addAttribute("placedCompanyName", placedCompanyName);
    model.addAttribute("applications", applications);
    model.addAttribute("totalApplications", totalApplications);
    model.addAttribute("shortlistedCount", shortlistedCount);
    model.addAttribute("oaPendingCount", oaPendingCount);


    return "pages/dashboard";
}
}