package com.crms.placement.controller;

import com.crms.placement.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HrDashboardController {

    @GetMapping("/hr/dashboard")
    public String hrDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equalsIgnoreCase("HR")) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "pages/hr_dashboard";
    }
}