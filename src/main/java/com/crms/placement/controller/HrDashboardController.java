package com.crms.placement.controller;

import com.crms.placement.model.*;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.OpportunityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/hr")
public class HrDashboardController {

    private final OpportunityService opportunityService;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;

    public HrDashboardController(OpportunityService opportunityService, CompanyRepository companyRepository, StudentRepository studentRepository) {
        this.opportunityService = opportunityService;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Mock HR User Data (Company ID 1 for now)
        Company company = companyRepository.findById(1).orElseGet(() -> {
            Company c = new Company("TechCorp");
            c.setCompany_id(1);
            return companyRepository.save(c);
        });

        // Get all jobs and filter by company
        List<Opportunity> allJobs = opportunityService.getAllJobs();
        List<Map<String, Object>> activePostings = new ArrayList<>();

        for (Opportunity job : allJobs) {
            if (job.getCompany() != null && job.getCompany().getCompany_id().equals(company.getCompany_id())) {
                Map<String, Object> posting = new HashMap<>();
                posting.put("job", job);
                // In a real scenario, we'd fetch actual applicant counts
                List<Application> applicants = opportunityService.getApplicantsByJob(job.getOpportunity_id());
                posting.put("applicantCount", applicants.size());
                
                boolean isOpen = job.getApplicationDeadline() != null && job.getApplicationDeadline().isAfter(LocalDateTime.now());
                posting.put("isOpen", isOpen);
                
                activePostings.add(posting);
            }
        }

        model.addAttribute("company", company);
        model.addAttribute("activePostings", activePostings);

        return "pages/hr-dashboard";
    }

    @PostMapping("/job/post")
    public String postJob(
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam String type,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam Double ctc,
            @RequestParam Double minCgpa,
            @RequestParam Integer vacancies,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDeadline,
            RedirectAttributes redirectAttributes) {

        // Mock company for HR session
        Company company = companyRepository.findById(1).orElseThrow(() -> new RuntimeException("Company not found"));

        try {
            // GRASP: Creator (OpportunityBuilder creates Opportunity)
            // Creational Pattern: Builder Pattern
            Opportunity newJob = Opportunity.builder()
                    .withName(name)
                    .withRole(role)
                    .withType(type)
                    .withDescription(description)
                    .withLocation(location)
                    .withCtc(ctc)
                    .withMinCgpa(minCgpa)
                    .withVacancies(vacancies)
                    .withApplicationDeadline(applicationDeadline)
                    .withCompany(company)
                    .build();

            opportunityService.createOpportunity(newJob);
            redirectAttributes.addFlashAttribute("successMessage", "Job Posted Successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error posting job: " + e.getMessage());
        }

        return "redirect:/hr/dashboard";
    }

    @GetMapping("/job/{id}/applicants")
    public String viewApplicants(@PathVariable Integer id, Model model) {
        Opportunity job = opportunityService.getJobById(id);
        if (job == null) {
            return "redirect:/hr/dashboard";
        }

        List<Application> applications = opportunityService.getApplicantsByJob(id);
        
        List<Map<String, Object>> applicantDetails = new ArrayList<>();
        List<Map<String, Object>> rankList = new ArrayList<>();
        Random random = new Random();

        for (Application app : applications) {
            Student student = studentRepository.findById(app.getStudentId().longValue()).orElse(null);
            if (student != null) {
                Map<String, Object> details = new HashMap<>();
                details.put("application", app);
                details.put("student", student);
                applicantDetails.add(details);

                // Add to rank list if OA is completed
                if (app.getStatus() == ApplicationStatus.OA_COMPLETED) {
                    Map<String, Object> rankEntry = new HashMap<>();
                    rankEntry.put("application", app);
                    rankEntry.put("student", student);
                    // Generate random OA score between 60 and 100
                    rankEntry.put("oaScore", 60 + random.nextInt(41));
                    rankList.add(rankEntry);
                }
            }
        }

        // Sort rank list by oaScore descending
        rankList.sort((a, b) -> Integer.compare((Integer) b.get("oaScore"), (Integer) a.get("oaScore")));

        model.addAttribute("job", job);
        model.addAttribute("applicants", applicantDetails);
        model.addAttribute("rankList", rankList);

        return "pages/hr-applicants";
    }

    @PostMapping("/application/{id}/status")
    @ResponseBody
    public Map<String, Object> updateApplicationStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            opportunityService.updateApplicationStatus(id, newStatus);
            response.put("success", true);
            response.put("message", "Status updated successfully.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid status value.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating status: " + e.getMessage());
        }
        return response;
    }
}
