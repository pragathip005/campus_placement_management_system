package com.crms.placement.controller;

import com.crms.placement.dto.AlumniHubView;
import com.crms.placement.entity.Alumni;
import com.crms.placement.entity.CareerHistory;
import com.crms.placement.entity.OAPrepHistory;
import com.crms.placement.model.Student;
import com.crms.placement.service.AlumniService;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AlumniPageController {

    private final AlumniService alumniService;
    private final StudentRepository studentRepository;

    public AlumniPageController(AlumniService alumniService, StudentRepository studentRepository) {
        this.alumniService = alumniService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/alumni-hub")
    public String alumniHub(Model model) {
        Student currentStudent = studentRepository.findById(1L).orElse(null);
        boolean isStudentPlaced = currentStudent != null && Boolean.TRUE.equals(currentStudent.getIsPlaced());
        String currentStudentCompany = (isStudentPlaced && currentStudent.getPlacedCompany() != null) 
                                        ? currentStudent.getPlacedCompany().getName() : "";

        var alumniDetails = alumniService.getAllAlumni().stream()
                .map(alumni -> {
                    var careerHistory = alumniService.getCareerHistoryForAlumni(alumni.getId());
                    var oaPrepHistory = alumniService.getOAPrepHistoryForAlumni(alumni.getId());

                    // Check if student can see contact details
                    boolean canSeeContact = isStudentPlaced &&
                            careerHistory.stream().anyMatch(career ->
                                    career.getCompany() != null &&
                                    career.getCompany().equalsIgnoreCase(currentStudentCompany));

                    return new AlumniHubView(alumni, careerHistory, oaPrepHistory, canSeeContact);
                })
                .toList();

        model.addAttribute("alumniDetails", alumniDetails);
        model.addAttribute("currentStudentCompany", currentStudentCompany);
        model.addAttribute("isStudentPlaced", isStudentPlaced);

        return "pages/alumni-hub";
    }

    @PostMapping("/alumni/save")
    public String saveAlumni(@ModelAttribute Alumni alumni) {
        alumniService.saveAlumni(alumni);
        return "redirect:/alumni";
    }

    @PostMapping("/alumni/career/save")
    public String saveCareerHistory(@ModelAttribute CareerHistory careerHistory) {
        alumniService.saveCareerHistory(careerHistory);
        return "redirect:/alumni";
    }

    @PostMapping("/alumni/prep/save")
    public String saveOAPrepHistory(@ModelAttribute OAPrepHistory prepHistory) {
        alumniService.saveOAPrepHistory(prepHistory);
        return "redirect:/alumni";
    }
}
