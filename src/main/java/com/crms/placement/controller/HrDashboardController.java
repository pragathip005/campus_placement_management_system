package com.crms.placement.controller;

import com.crms.placement.dto.RankListEntry;
import com.crms.placement.model.*;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.OpportunityService;
import com.crms.placement.service.RankListService;
import com.crms.placement.service.SupabaseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.crms.placement.service.SlotAllocationService;
import com.crms.placement.service.NotificationService;


import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/hr")
public class HrDashboardController {

    private final OpportunityService opportunityService;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final SupabaseService supabaseService;
    private final RankListService rankListService;
    private final SlotAllocationService slotAllocationService;
    private final NotificationService notificationService;
    

    public HrDashboardController(OpportunityService opportunityService,
                                  CompanyRepository companyRepository,
                                  StudentRepository studentRepository,
                                  SupabaseService supabaseService,
                                  RankListService rankListService,
                                  SlotAllocationService slotAllocationService,
                                  NotificationService notificationService
                                  ) {
        this.opportunityService = opportunityService;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
        this.supabaseService = supabaseService;
        this.rankListService = rankListService;
        this.slotAllocationService = slotAllocationService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Company company = companyRepository.findById(user.getUserId()).orElse(null);
        if (company == null) {
            model.addAttribute("error", "No company profile found.");
            model.addAttribute("activePostings", new ArrayList<>());
            return "pages/hr-dashboard";
        }

        List<Opportunity> allJobs = opportunityService.getAllJobs();
        List<Map<String, Object>> activePostings = new ArrayList<>();

        for (Opportunity job : allJobs) {
            if (job.getCompany() != null &&
                    job.getCompany().getCompany_id().equals(company.getCompany_id())) {
                Map<String, Object> posting = new HashMap<>();
                posting.put("job", job);
                List<Application> applicants =
                        opportunityService.getApplicantsByJob(job.getOpportunity_id());
                posting.put("applicantCount", applicants.size());
                boolean isOpen = job.getApplicationDeadline() != null
                        && job.getApplicationDeadline().isAfter(LocalDateTime.now());
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

        User user = (User) session.getAttribute("user");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Company company = companyRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

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
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error posting job: " + e.getMessage());
        }

        return "redirect:/hr/dashboard";
    }

    @GetMapping("/job/{id}/applicants")
    public String viewApplicants(HttpSession session,
                                  @PathVariable Integer id,
                                  @RequestParam(defaultValue = "score") String strategy,
                                  Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Opportunity job = opportunityService.getJobById(id);
        if (job == null) return "redirect:/hr/dashboard";

        List<Application> applications = opportunityService.getApplicantsByJob(id);

        List<Map<String, Object>> applicantDetails = new ArrayList<>();
        for (Application app : applications) {
            Student student = studentRepository
                    .findById(app.getStudentId().longValue()).orElse(null);
            if (student != null) {
                Map<String, Object> details = new HashMap<>();
                details.put("application", app);
                details.put("student", student);
                applicantDetails.add(details);
            }
        }

        // ✅ Real DB scores via RankListService with chosen strategy
        List<RankListEntry> rankList = rankListService.getRankList(id, strategy);

        model.addAttribute("job", job);
        model.addAttribute("applicants", applicantDetails);
        model.addAttribute("rankList", rankList);
        model.addAttribute("currentStrategy", strategy);
        model.addAttribute("strategyName",
                rankListService.resolveStrategy(strategy).getStrategyName());

        return "pages/hr-applicants";
    }

    // ✅ CSV download
    @GetMapping("/job/{id}/ranklist/download")
    public void downloadRankList(HttpSession session,
                                  @PathVariable Integer id,
                                  @RequestParam(defaultValue = "score") String strategy,
                                  HttpServletResponse response) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect("/login");
            return;
        }

        Opportunity job = opportunityService.getJobById(id);
        String jobName = job != null ? job.getName().replaceAll("\\s+", "_") : "job";

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"ranklist_" + jobName + "_" + strategy + ".csv\"");

        PrintWriter writer = response.getWriter();
        rankListService.writeRankListCsv(id, strategy, writer);
    }

    // ✅ Update status only — no email (Person 4's slot allocation handles interview notification)
    @PostMapping("/application/{id}/status")
    @ResponseBody
    public Map<String, Object> updateApplicationStatus(
            HttpSession session,
            @PathVariable Integer id,
            @RequestParam String status) {

        Map<String, Object> response = new HashMap<>();

        User user = (User) session.getAttribute("user");
        if (user == null || !"HR".equalsIgnoreCase(user.getRole())) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        try {
            Application application = opportunityService.getApplicationById(id);
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            opportunityService.updateApplicationStatus(id, newStatus);

            Student student = studentRepository
        .findById(application.getStudentId().longValue())
        .orElse(null);

if (student == null) {
    throw new RuntimeException("Student not found for application " + id);
}

String email = student.getEmail();
String name = student.getName();

            
            String company = application.getOpportunity().getCompany().getName();
            String role = application.getOpportunity().getRole();


            if (newStatus == ApplicationStatus.INTERVIEW) {
               slotAllocationService.allocateSlots();
            }

            if (newStatus == ApplicationStatus.SELECTED) {
               notificationService.sendOfferLetter(email, name, company, role);
               // Sync student.isPlaced and student.placedCompany so alumni hub unlock works
               student.setIsPlaced(true);
               student.setPlacedCompany(application.getOpportunity().getCompany());
               studentRepository.save(student);
            }


            if (newStatus == ApplicationStatus.REJECTED) {
               notificationService.sendRejectionEmail(email, name, company);
            }
            response.put("success", true);
            response.put("message", "Status updated successfully.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid status value.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }
}