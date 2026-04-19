package com.crms.placement.controller;

import com.crms.placement.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
public String dashboard(HttpSession session, Model model) {

    User sessionUser = (User) session.getAttribute("user");

    if (sessionUser == null) {
        return "redirect:/login";
    }

    User user = sessionUser; // keep simple

    model.addAttribute("user", user);

    if ("HR".equalsIgnoreCase(user.getRole())) {
        return "pages/hr/dashboard";
    }

    if ("STUDENT".equalsIgnoreCase(user.getRole())) {
        return "redirect:/student/dashboard";
    }

    return "redirect:/login";
}
}