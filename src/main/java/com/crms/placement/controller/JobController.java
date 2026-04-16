package com.crms.placement.controller;

import com.crms.placement.service.OpportunityService;
import com.crms.placement.model.Student;
import com.crms.placement.model.Opportunity;
import com.crms.placement.entity.Alumni;
import com.crms.placement.model.Application;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.crms.placement.repository.AlumniRepository;
import com.crms.placement.repository.ApplicationRepository;
import java.util.List;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class JobController {

    private final OpportunityService service;
    private final StudentRepository studentRepository;
    private final AlumniRepository alumniRepository;
    private final ApplicationRepository applicationRepository;

    public JobController(OpportunityService service,
                     StudentRepository studentRepository,
                     AlumniRepository alumniRepository,
                     ApplicationRepository applicationRepository) {
        this.service = service;
        this.studentRepository = studentRepository;
        this.alumniRepository = alumniRepository;
        this.applicationRepository = applicationRepository;
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
            jobMap.put("isEligible", service.isEligible(studentId, job.getOpportunity_id()));
            return jobMap;
        }).toList();

        Student student = studentRepository.findById(1L).orElse(null);

        model.addAttribute("jobs", jobsWithStatus);
        model.addAttribute("studentId", studentId);
        model.addAttribute("user", student);

        return "pages/job-board";
    }

    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable Integer id, Model model) {
        Opportunity job = service.getJobById(id);

        long applicationsCount = applicationRepository.countByOpportunityId(id);

        model.addAttribute("applicationsCount", applicationsCount);

        if (job == null) {
            return "redirect:/job-board";
        }

        // ✅ Job status
        boolean isOpen = job.getApplicationDeadline().isAfter(LocalDateTime.now());

        // ✅ Alumni data
        long alumniCount = alumniRepository
                .countByCurrentCompanyIgnoreCase(job.getCompany().getName());

        List<Alumni> alumniList = alumniRepository
                .findByCurrentCompanyIgnoreCase(job.getCompany().getName());

        // ✅ Student (temporary hardcoded)
        Student currentStudent = studentRepository.findById(1L).orElse(null);
        if (currentStudent == null) {
            currentStudent = new Student();
            currentStudent.setStudentId(1L);
            currentStudent.setName("Nishita");
            currentStudent.setCgpa(9.29);
            currentStudent.setBacklogCount(0);
        }

        boolean isEligible = service.isEligible(currentStudent.getStudentId().intValue(), id);
        boolean hasApplied = service.isApplied(currentStudent.getStudentId().intValue(), id);


        // ✅ Add to model
        model.addAttribute("job", job);
        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("isEligible", isEligible);
        model.addAttribute("hasApplied", hasApplied);
        model.addAttribute("alumniCount", alumniCount);
        model.addAttribute("alumni", alumniList);
        model.addAttribute("isOpen", isOpen);

        return "pages/job-detail";
    }

    @PostMapping("/job/{id}/apply")
    @ResponseBody
    public Map<String, Object> applyToJob(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            // For now, using hardcoded student ID. In production, get from session/auth
            Integer studentId = 1;

            System.out.println("🔥 APPLY ENDPOINT HIT - studentId: " + studentId + ", opportunityId: " + id);

            // Check eligibility first
            if (!service.isEligible(studentId, id)) {
                System.out.println("❌ Student not eligible");
                response.put("success", false);
                response.put("message", "You don't meet the eligibility criteria for this job");
                return response;
            }

            // Check if already applied
            if (service.isApplied(studentId, id)) {
                System.out.println("❌ Already applied");
                response.put("success", false);
                response.put("message", "You have already applied for this job");
                return response;
            }

            // Create application
            System.out.println("✅ Creating application...");
            Application application = service.applyToJob(studentId, id);
            System.out.println("✅ Application created - ID: " + application.getApplicationId());

            response.put("success", true);
            response.put("message", "Application submitted successfully");
            response.put("applicationId", application.getApplicationId());

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "An error occurred while processing your application: " + e.getMessage());
        }

        return response;
    }
}