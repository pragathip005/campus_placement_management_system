package com.crms.placement.controller;

import com.crms.placement.entity.Alumni;
import com.crms.placement.entity.CareerHistory;
import com.crms.placement.entity.OAPrepHistory;
import com.crms.placement.model.User;
import com.crms.placement.repository.CareerHistoryRepository;
import com.crms.placement.service.AlumniService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/alumni/dashboard")
public class AlumniPortalController {

    private final AlumniService alumniService;
    private final CareerHistoryRepository careerHistoryRepository;

    public AlumniPortalController(AlumniService alumniService, CareerHistoryRepository careerHistoryRepository) {
        this.alumniService = alumniService;
        this.careerHistoryRepository = careerHistoryRepository;
    }

    @GetMapping
    public String alumniPortal(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Alumni currentAlumni = alumniService.getAlumniById(user.getUserId()).orElseGet(() -> {
            Alumni empty = new Alumni();
            empty.setId(user.getUserId());
            empty.setEmail(user.getEmail());
            return empty;
        });

        model.addAttribute("alumni", currentAlumni);
        model.addAttribute("username", currentAlumni.getName() != null ? currentAlumni.getName() : user.getName());
        model.addAttribute("careerHistoryList",
                currentAlumni.getId() != null ? alumniService.getCareerHistoryForAlumni(currentAlumni.getId()) : java.util.List.of());
        model.addAttribute("oaPrepHistoryList",
                currentAlumni.getId() != null ? alumniService.getOAPrepHistoryForAlumni(currentAlumni.getId()) : java.util.List.of());
        model.addAttribute("careerHistory", new CareerHistory());
        model.addAttribute("oaPrep", new OAPrepHistory());

        return "pages/alumni-portal";
    }

    @PostMapping("/save")
    public String saveAlumniProfile(@ModelAttribute Alumni alumni, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Alumni toSave = alumniService.getAlumniById(user.getUserId()).orElseGet(() -> {
            Alumni fresh = new Alumni();
            fresh.setId(user.getUserId());
            fresh.setEmail(user.getEmail());
            return fresh;
        });

        String previousCompany = toSave.getCurrentCompany();

        toSave.setName(alumni.getName());
        toSave.setPhone(alumni.getPhone());
        toSave.setGraduationYear(alumni.getGraduationYear());
        toSave.setPlacementStatus(alumni.getPlacementStatus());
        toSave.setCurrentCompany(alumni.getCurrentCompany());
        toSave.setCurrentJobRole(alumni.getCurrentJobRole());
        toSave.setLinkedinUrl(alumni.getLinkedinUrl());
        toSave.setHigherStudiesUniversity(alumni.getHigherStudiesUniversity());
        toSave.setHigherStudiesCourse(alumni.getHigherStudiesCourse());
        alumniService.saveAlumni(toSave);

        syncCurrentCareerEntry(user.getUserId(), previousCompany, alumni.getCurrentCompany(), alumni.getCurrentJobRole());
        return "redirect:/alumni/dashboard";
    }

    private void syncCurrentCareerEntry(Long alumniId, String previousCompany, String newCompany, String newRole) {
        CareerHistory existing = careerHistoryRepository
                .findFirstByAlumniIdAndIsCurrentTrue(alumniId).orElse(null);

        boolean companyChanged = newCompany != null && !newCompany.isBlank()
                && !newCompany.equalsIgnoreCase(previousCompany);

        if (newCompany == null || newCompany.isBlank()) {
            // Company cleared — mark existing current entry as ended
            if (existing != null) {
                existing.setIsCurrent(false);
                existing.setEndDate(LocalDate.now());
                careerHistoryRepository.save(existing);
            }
            return;
        }

        if (existing != null && !companyChanged) {
            boolean roleChanged = newRole != null && !newRole.isBlank()
                    && !newRole.equalsIgnoreCase(existing.getRole());
            if (!roleChanged) return; // nothing meaningful changed

            // Same company, different role = promotion — close old entry, open new one
            existing.setIsCurrent(false);
            existing.setEndDate(LocalDate.now());
            careerHistoryRepository.save(existing);

            CareerHistory promoted = new CareerHistory();
            promoted.setAlumniId(alumniId);
            promoted.setCompany(newCompany);
            promoted.setRole(newRole);
            promoted.setIsCurrent(true);
            promoted.setStartDate(LocalDate.now());
            careerHistoryRepository.save(promoted);
            return;
        }

        if (existing != null && companyChanged) {
            // Moved to a new company — close out the old entry
            existing.setIsCurrent(false);
            existing.setEndDate(LocalDate.now());
            careerHistoryRepository.save(existing);
        }

        // Create a new current entry for the new company
        CareerHistory entry = new CareerHistory();
        entry.setAlumniId(alumniId);
        entry.setCompany(newCompany);
        entry.setRole(newRole != null ? newRole : "");
        entry.setIsCurrent(true);
        entry.setStartDate(LocalDate.now());
        careerHistoryRepository.save(entry);
    }

    @PostMapping("/career/save")
    public String saveCareerHistory(@ModelAttribute CareerHistory careerHistory, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        careerHistory.setAlumniId(user.getUserId());
        alumniService.saveCareerHistory(careerHistory);
        return "redirect:/alumni/dashboard";
    }

    @PostMapping("/prep/save")
    public String saveOAPrepHistory(@ModelAttribute OAPrepHistory oaPrep, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        oaPrep.setAlumniId(user.getUserId());
        alumniService.saveOAPrepHistory(oaPrep);
        return "redirect:/alumni/dashboard";
    }
}
