package com.crms.placement.controller;

import com.crms.placement.model.*;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.OpportunityService;
import com.crms.placement.service.SupabaseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

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
    private final SupabaseService supabaseService;

    public HrDashboardController(OpportunityService opportunityService, CompanyRepository companyRepository, StudentRepository studentRepository, SupabaseService supabaseService) {
        this.opportunityService = opportunityService;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
        this.supabaseService = supabaseService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Company company = companyRepository.findById(user.getUserId()).orElse(null);
        if (company == null) {
            model.addAttribute("error", "No company profile found.");
            model.addAttribute("activePostings", new ArrayList<>());
            return "pages/hr-dashboard";
        }

        // Get all jobs and filter by company
        List<Opportunity> allJobs = opportunityService.getAllJobs();
        List<Map<String, Object>> activePostings = new ArrayList<>();

        for (Opportunity job : allJobs) {
            if (job.getCompany() != null && job.getCompany().getCompany_id().equals(company.getCompany_id())) {
                Map<String, Object> posting = new HashMap<>();
                posting.put("job", job);
                // Fetch actual applicant counts
                List<Application> applicants = opportunityService.getApplicantsByJob(job.getOpportunity_id());
                posting.put("applicantCount", applicants.size());
                
                boolean isOpen = job.getApplicationDeadline() != null && job.getApplicationDeadline().isAfter(LocalDateTime.now());
                posting.put("isOpen", isOpen);
                
                activePostings.add(posting);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("company", company);
        model.addAttribute("activePostings", activePostings);

        return "pages/hr-dashboard";
    }

    @PostMapping("/job/post")
    public String postJob(
            HttpSession session,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam String type,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam(required = false) Double ctc,
            @RequestParam(required = false) Double stipend,
            @RequestParam(required = false) Integer duration,
            @RequestParam Double minCgpa,
            @RequestParam Integer vacancies,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDeadline,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime oaDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewDate,
            @RequestParam(required = false) Double shortlistingSuccessRate,
            @RequestParam(required = false) String eligibleBranches,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile jdPdf,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        // Fetch company for HR session
        Company company = companyRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("Company not found"));

        try {
            List<String> branchesList = new ArrayList<>();
            if (eligibleBranches != null && !eligibleBranches.trim().isEmpty()) {
                for (String branch : eligibleBranches.split(",")) {
                    branchesList.add(branch.trim());
                }
            }

            String uploadedJdUrl = null;
            if (jdPdf != null && !jdPdf.isEmpty()) {
                uploadedJdUrl = supabaseService.uploadFile(jdPdf, true);
            }

            // GRASP: Creator (OpportunityBuilder creates Opportunity)
            // Creational Pattern: Builder Pattern
            Opportunity newJob = Opportunity.builder()
                    .withName(name)
                    .withRole(role)
                    .withType(type)
                    .withDescription(description)
                    .withLocation(location)
                    .withCtc(ctc)
                    .withStipend(stipend)
                    .withDuration(duration)
                    .withMinCgpa(minCgpa)
                    .withVacancies(vacancies)
                    .withApplicationDeadline(applicationDeadline)
                    .withOaDate(oaDate)
                    .withInterviewDate(interviewDate)
                    .withHasOA(oaDate != null)
                    .withHasInterview(interviewDate != null)
                    .withShortlistingSuccessRate(shortlistingSuccessRate)
                    .withEligibleBranches(branchesList)
                    .withCompany(company)
                    .withJdUrl(uploadedJdUrl)
                    .build();

            opportunityService.createOpportunity(newJob);
            redirectAttributes.addFlashAttribute("successMessage", "Job Posted Successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error posting job: " + e.getMessage());
        }

        return "redirect:/hr/dashboard";
    }

    @GetMapping("/job/{id}/applicants")
    public String viewApplicants(HttpSession session, @PathVariable Integer id, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
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
            HttpSession session,
            @PathVariable Integer id,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }
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