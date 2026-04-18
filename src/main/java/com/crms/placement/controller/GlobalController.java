package com.crms.placement.controller;

import com.crms.placement.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("user")
    public User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }
}