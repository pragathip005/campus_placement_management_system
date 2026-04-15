package com.crms.placement.controller;

import com.crms.placement.service.PreparationHubService;
import com.crms.placement.model.Student;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PreparationHubController {

    private final PreparationHubService preparationHubService;
    private final StudentRepository studentRepository;

    public PreparationHubController(PreparationHubService preparationHubService, StudentRepository studentRepository) {
        this.preparationHubService = preparationHubService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/preparation-hub")
    public String preparationHub(Model model) {
        // Get all preparation data grouped by company
        var preparationData = preparationHubService.getPreparationHubData();
        Student student = studentRepository.findById(1L).orElse(null);

        model.addAttribute("companies", preparationData);
        model.addAttribute("user", student);

        return "pages/preparation-hub";
    }

    @GetMapping("/oa-questions")
    public String oaQuestions(@RequestParam(value = "company", required = false) String companyName, Model model) {
        if (companyName != null && !companyName.isEmpty()) {
            var questions = preparationHubService.getOAQuestionsByCompanyName(companyName);
            model.addAttribute("questions", questions);
            model.addAttribute("companyName", companyName);
        } else {
            var allQuestions = preparationHubService.getAllOAQuestions();
            model.addAttribute("questions", allQuestions);
            model.addAttribute("companyName", "All Companies");
        }

        Student student = studentRepository.findById(1L).orElse(null);
        model.addAttribute("user", student);
        return "pages/oa-questions";
    }

    @GetMapping("/interview-experiences")
    public String interviewExperiences(@RequestParam(value = "company", required = false) String companyName, Model model) {
        if (companyName != null && !companyName.isEmpty()) {
            var experiences = preparationHubService.getInterviewExperiencesByCompanyName(companyName);
            model.addAttribute("experiences", experiences);
            model.addAttribute("companyName", companyName);
        } else {
            var allExperiences = preparationHubService.getAllInterviewExperiences();
            model.addAttribute("experiences", allExperiences);
            model.addAttribute("companyName", "All Companies");
        }

        Student student = studentRepository.findById(1L).orElse(null);
        model.addAttribute("user", student);
        return "pages/interview-experiences";
    }
}

