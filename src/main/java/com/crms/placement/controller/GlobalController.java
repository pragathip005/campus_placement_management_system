package com.crms.placement.controller;

import com.crms.placement.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("user")
    public User getLoggedInUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        System.out.println("GlobalController: session user = " + (user != null ? user.getName() : "null"));
        return user;
    }
}