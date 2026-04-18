package com.crms.placement.controller;
import com.crms.placement.model.Student;
import com.crms.placement.model.User;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;
import com.crms.placement.service.SupabaseService;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SupabaseService supabaseService;
    
    public UserController(StudentRepository studentRepository,
                      UserRepository userRepository,
                      SupabaseService supabaseService) {
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
    this.supabaseService = supabaseService;
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
            @RequestParam(required = false) MultipartFile resumeFile,
            @RequestParam(required = false) String password,
            HttpSession session
    ) {

        System.out.println("UPLOAD HIT"); // 🔥 ADD THIS HERE

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Long userId = user.getUserId();
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        // update phone
        student.setPhone(phone);
        // 🔥 NEW: resume upload logic
        System.out.println("File received: " + 
        (resumeFile != null ? resumeFile.getOriginalFilename() : "NULL"));
        if (resumeFile != null && !resumeFile.isEmpty()) {
            System.out.println("Uploading to Supabase...");
            String resumeUrl = supabaseService.uploadResume(resumeFile);
            System.out.println("Supabase URL: " + resumeUrl);
            student.setResumeUrl(resumeUrl);
        }

        System.out.println("Resume saved in DB: " + student.getResumeUrl());
        studentRepository.save(student);

        // password update
        if (password != null && !password.isEmpty()) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
            userRepository.save(user);
        }

        return "redirect:/profile";
    }
}