package com.crms.placement.controller;

import com.crms.placement.model.Student;
import com.crms.placement.model.User;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public UserController(StudentRepository studentRepository,
                          UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // ✅ SHOW PROFILE PAGE
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) return "redirect:/login";

        // 🔥 IMPORTANT: since you used @MapsId
        Student student = studentRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        model.addAttribute("student", student);

        return "pages/profile";
    }

    // ✅ UPDATE PROFILE
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String phone,
            @RequestParam String resumeUrl,
            @RequestParam String password,
            HttpSession session
    ) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) return "redirect:/login";

        Long userId = user.getUserId();

        // 🔹 update student table
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setPhone(phone);
        student.setResumeUrl(resumeUrl);

        studentRepository.save(student);

        // 🔹 update password (ONLY if not empty)
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
            userRepository.save(user);
        }

        return "redirect:/profile";
    }
}