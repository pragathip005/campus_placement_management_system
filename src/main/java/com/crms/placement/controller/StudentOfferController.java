package com.crms.placement.controller;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.User;
import com.crms.placement.service.OpportunityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentOfferController {

    private final OpportunityService opportunityService;

    public StudentOfferController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    // ✅ OPEN OFFER PAGE
    @GetMapping("/offer")
    public String viewOffer(@RequestParam Integer applicationId,
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Application app = opportunityService.getApplicationById(applicationId);

        if (app == null) {
            return "redirect:/student/dashboard";
        }

        // ✅ IMPORTANT: pass BOTH
        model.addAttribute("application", app);
        model.addAttribute("applicationId", app.getApplicationId());

        return "pages/offer-page";
    }

    // ✅ ACCEPT OFFER
    @PostMapping("/offer/accept")
    public String acceptOffer(@RequestParam Integer applicationId) {
        opportunityService.updateApplicationStatus(
                applicationId,
                ApplicationStatus.OFFER_ACCEPTED
        );
        return "redirect:/student/dashboard";
    }

    // ❌ REJECT OFFER
    @PostMapping("/offer/reject")
    public String rejectOffer(@RequestParam Integer applicationId) {
        opportunityService.updateApplicationStatus(
                applicationId,
                ApplicationStatus.OFFER_REJECTED
        );
        return "redirect:/student/dashboard";
    }
}