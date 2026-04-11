package com.crms.placement.controller;

import com.crms.placement.service.OpportunityService;
import com.crms.placement.model.User;
import com.crms.placement.model.Opportunity;
import com.crms.placement.model.Application;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class JobController {

    private final OpportunityService service;

    public JobController(OpportunityService service) {
        this.service = service;
    }

    @GetMapping("/job-board")
    public String jobBoard(Model model) {
        // For now, using hardcoded student. In production, get from session/auth
        Integer studentId = 1;

        var jobs = service.getAllJobs();
        var jobsWithStatus = jobs.stream().map(job -> {
            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("job", job);
            jobMap.put("isApplied", service.isApplied(studentId, job.getOpportunity_id()));
            return jobMap;
        }).toList();

        model.addAttribute("jobs", jobsWithStatus);
        model.addAttribute("studentId", studentId);
        model.addAttribute("user", new User("Nishita", "STUDENT"));

        return "pages/job-board";
    }

    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable Integer id, Model model) {
        Opportunity job = service.getJobById(id);

        if (job == null) {
            return "redirect:/job-board";
        }

        // For now, using hardcoded student. In production, get from session/auth
        User currentStudent = new User();
        currentStudent.setUserId(1L);
        currentStudent.setName("Nishita");
        currentStudent.setRole("STUDENT");
        currentStudent.setCgpa(9.29);
        currentStudent.setBacklogCount(0);

        // Check eligibility
        boolean isEligible = service.isEligible(currentStudent.getUserId().intValue(), id);
        boolean hasApplied = service.isApplied(currentStudent.getUserId().intValue(), id);

        // Get statistics
        long alumniCount = 30; // TODO: Integrate with AlumniService
        double shortlistRate = service.calculateShortlistSuccessRate(id);

        model.addAttribute("job", job);
        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("isEligible", isEligible);
        model.addAttribute("hasApplied", hasApplied);
        model.addAttribute("alumniCount", alumniCount);
        model.addAttribute("shortlistSuccessRate", shortlistRate);
        model.addAttribute("alumni", null); // TODO: Integrate with AlumniService

        return "pages/job-detail";
    }

    @PostMapping("/job/{id}/apply")
    @ResponseBody
    public Map<String, Object> applyToJob(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            // For now, using hardcoded student ID. In production, get from session/auth
            Integer studentId = 1;

            // Check eligibility first
            if (!service.isEligible(studentId, id)) {
                response.put("success", false);
                response.put("message", "You don't meet the eligibility criteria for this job");
                return response;
            }

            // Check if already applied
            if (service.isApplied(studentId, id)) {
                response.put("success", false);
                response.put("message", "You have already applied for this job");
                return response;
            }

            // Create application
            Application application = service.applyToJob(studentId, id);
            response.put("success", true);
            response.put("message", "Application submitted successfully");
            response.put("applicationId", application.getApplicationId());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while processing your application");
        }

        return response;
    }
}