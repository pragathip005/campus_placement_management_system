package com.crms.placement.controller;

import com.crms.placement.entity.Alumni;
import com.crms.placement.entity.CareerHistory;
import com.crms.placement.entity.OAPrepHistory;
import com.crms.placement.service.AlumniService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/alumni/portal")
public class AlumniPortalController {

    private final AlumniService alumniService;

    public AlumniPortalController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @GetMapping
    public String alumniPortal(Model model) {
        // Mock authentication for the portal. Ideally, we get email from session User.
        // We will fetch the first alumni, or create an empty mock one if none exist.
        Alumni currentAlumni = alumniService.getAllAlumni().stream().findFirst().orElse(new Alumni());
        
        if (currentAlumni.getId() == null) {
            currentAlumni.setName("Mock Alumni");
            currentAlumni.setEmail("mockalumni@example.com");
            currentAlumni.setPlacementStatus("PLACED");
        }

        model.addAttribute("alumni", currentAlumni);
        
        // Load partial data to show in history lists
        if (currentAlumni.getId() != null) {
            model.addAttribute("careerHistoryList", alumniService.getCareerHistoryForAlumni(currentAlumni.getId()));
            model.addAttribute("oaPrepHistoryList", alumniService.getOAPrepHistoryForAlumni(currentAlumni.getId()));
        }

        // Blank objects for the forms
        model.addAttribute("careerHistory", new CareerHistory());
        model.addAttribute("oaPrep", new OAPrepHistory());

        return "pages/alumni-portal";
    }

    @PostMapping("/save")
    public String saveAlumniProfile(@ModelAttribute Alumni alumni) {
        // Find existing alumni by ID to merge updates securely
        if (alumni.getId() != null) {
            alumniService.getAlumniById(alumni.getId()).ifPresent(existing -> {
                existing.setName(alumni.getName());
                existing.setPhone(alumni.getPhone());
                existing.setGraduationYear(alumni.getGraduationYear());
                existing.setPlacementStatus(alumni.getPlacementStatus());
                existing.setCurrentCompany(alumni.getCurrentCompany());
                existing.setCurrentJobRole(alumni.getCurrentJobRole());
                existing.setLinkedinUrl(alumni.getLinkedinUrl());
                existing.setHigherStudiesUniversity(alumni.getHigherStudiesUniversity());
                existing.setHigherStudiesCourse(alumni.getHigherStudiesCourse());
                alumniService.saveAlumni(existing);
            });
        } else {
            alumniService.saveAlumni(alumni);
        }
        return "redirect:/alumni/portal";
    }

    @PostMapping("/career/save")
    public String saveCareerHistory(@ModelAttribute CareerHistory careerHistory, Long alumniId) {
        if (alumniId != null) {
            careerHistory.setAlumniId(alumniId);
            alumniService.saveCareerHistory(careerHistory);
        }
        return "redirect:/alumni/portal";
    }

    @PostMapping("/prep/save")
    public String saveOAPrepHistory(@ModelAttribute OAPrepHistory oaPrep, Long alumniId) {
        if (alumniId != null) {
            oaPrep.setAlumniId(alumniId);
            alumniService.saveOAPrepHistory(oaPrep);
        }
        return "redirect:/alumni/portal";
    }
}
