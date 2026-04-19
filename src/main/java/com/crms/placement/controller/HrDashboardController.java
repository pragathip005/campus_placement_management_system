package com.crms.placement.controller;

import com.crms.placement.model.*;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.OpportunityService;
import jakarta.servlet.http.HttpSession;
import com.crms.placement.service.SupabaseStorageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/hr")
public class HrDashboardController {

    private final OpportunityService opportunityService;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final SupabaseStorageService supabaseStorageService;


    public HrDashboardController(OpportunityService opportunityService, CompanyRepository companyRepository, StudentRepository studentRepository, SupabaseStorageService supabaseStorageService) {
        this.opportunityService = opportunityService;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    // =====================================================
    // ✅ KEEP YOUR ORIGINAL SESSION-BASED DASHBOARD (FIXED)
    // =====================================================
    @GetMapping("/dashboard")
    public String hrDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equalsIgnoreCase("HR")) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // OPTIONAL: if HR company is tied to user later
        return "pages/hr-dashboard";
    }

    // =====================================================
    // ✅ JOB LISTING (NO HARDCODED COMPANY ID)
    // =====================================================
    @GetMapping("/jobs")
    public String viewJobs(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equalsIgnoreCase("HR")) {
            return "redirect:/login";
        }

        List<Opportunity> allJobs = opportunityService.getAllJobs();
        List<Map<String, Object>> activePostings = new ArrayList<>();

        for (Opportunity job : allJobs) {

            if (job.getCompany() == null) continue;

            Map<String, Object> posting = new HashMap<>();
            posting.put("job", job);

            List<Application> applicants =
                    opportunityService.getApplicantsByJob(job.getOpportunity_id());

            posting.put("applicantCount", applicants.size());

            boolean isOpen = job.getApplicationDeadline() != null &&
                    job.getApplicationDeadline().isAfter(LocalDateTime.now());

            posting.put("isOpen", isOpen);

            activePostings.add(posting);
        }

        model.addAttribute("activePostings", activePostings);

        return "pages/hr-dashboard";
    }

    // =====================================================
    // ✅ POST JOB (NO HARDCODED COMPANY)
    // =====================================================
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

        if (user == null || !user.getRole().equalsIgnoreCase("HR")) {
            return "redirect:/login";
        }

        try {
            Company company = companyRepository.findById(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Company not found for HR"));

            List<String> branchesList = new ArrayList<>();
            if (eligibleBranches != null && !eligibleBranches.trim().isEmpty()) {
                for (String branch : eligibleBranches.split(",")) {
                    branchesList.add(branch.trim());
                }
            }

            String uploadedJdUrl = null;
            if (jdPdf != null && !jdPdf.isEmpty()) {
                uploadedJdUrl = supabaseStorageService.uploadFile(jdPdf);
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

            redirectAttributes.addFlashAttribute("successMessage",
                    "Job Posted Successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error posting job: " + e.getMessage());
        }

        return "redirect:/hr/dashboard";
    }

    // =====================================================
    // ✅ VIEW APPLICANTS (SESSION SAFE)
    // =====================================================
    @GetMapping("/job/{id}/applicants")
    public String viewApplicants(HttpSession session,
                                 @PathVariable Integer id,
                                 Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equalsIgnoreCase("HR")) {
            return "redirect:/login";
        }

        Opportunity job = opportunityService.getJobById(id);
        if (job == null) return "redirect:/hr/dashboard";

        List<Application> applications =
                opportunityService.getApplicantsByJob(id);

        List<Map<String, Object>> applicantDetails = new ArrayList<>();
        List<Map<String, Object>> rankList = new ArrayList<>();
        Random random = new Random();

        for (Application app : applications) {

            Student student =
                    studentRepository.findById(app.getStudentId().longValue()).orElse(null);

            if (student != null) {
                Map<String, Object> details = new HashMap<>();
                details.put("application", app);
                details.put("student", student);
                applicantDetails.add(details);

                if (app.getStatus() == ApplicationStatus.TEST_COMPLETED) {
                    Map<String, Object> rankEntry = new HashMap<>();
                    rankEntry.put("application", app);
                    rankEntry.put("student", student);
                    rankEntry.put("oaScore", 60 + random.nextInt(41));
                    rankList.add(rankEntry);
                }
            }
        }

        rankList.sort((a, b) ->
                Integer.compare((Integer) b.get("oaScore"), (Integer) a.get("oaScore")));

        model.addAttribute("job", job);
        model.addAttribute("applicants", applicantDetails);
        model.addAttribute("rankList", rankList);

        return "pages/hr-applicants";
    }

    // =====================================================
    // ✅ UPDATE STATUS (UNCHANGED BUT SAFE)
    // =====================================================
    @PostMapping("/application/{id}/status")
    @ResponseBody
    public Map<String, Object> updateApplicationStatus(
            HttpSession session,
            @PathVariable Integer id,
            @RequestParam String status) {

        Map<String, Object> response = new HashMap<>();

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equalsIgnoreCase("HR")) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        try {
            ApplicationStatus newStatus =
                    ApplicationStatus.valueOf(status.toUpperCase());

            opportunityService.updateApplicationStatus(id, newStatus);

            response.put("success", true);
            response.put("message", "Status updated successfully.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }
}