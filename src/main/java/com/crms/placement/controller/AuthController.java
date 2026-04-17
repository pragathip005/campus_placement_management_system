package com.crms.placement.controller;

import com.crms.placement.model.Student;
import com.crms.placement.model.User;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.LoginService;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final LoginService loginService;
    private final StudentRepository studentRepository;

    public AuthController(LoginService loginService, StudentRepository studentRepository) {
        this.loginService = loginService;
        this.studentRepository = studentRepository;
    }

    // show login page
    @GetMapping("/login")
    public String showLogin() {
        return "pages/login";
    }

    // handle login
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session,
            Model model
    ) {
        try {
            User user = loginService.login(email, password, role);
            System.out.println("LOGGED USER NAME: " + user.getName());

            // ✅ STORE IN SESSION (IMPORTANT)
            session.setAttribute("loggedInUser", user);

            return "redirect:/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pages/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate(); // ❗ clears everything
        System.out.println("LOGOUT: session invalidated");

        return "redirect:/login";
    }


    @GetMapping("/register")
    public String showRegister() {
        return "pages/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String srn,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer batchYear,
            @RequestParam(required = false) Double sgpaSem1,
            @RequestParam(required = false) Double sgpaSem2,
            @RequestParam(required = false) Double sgpaSem3,
            @RequestParam(required = false) Double sgpaSem4,
            @RequestParam(required = false) Double sgpaSem5,
            @RequestParam(required = false) Double sgpaSem6,
            @RequestParam(required = false) Double sgpaSem7,
            @RequestParam(required = false) String resumeUrl,
            @RequestParam(required = false) Double cgpa,
            @RequestParam(required = false) Integer backlogCount,
            HttpSession session,
            Model model
    ) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);

            if (role.equalsIgnoreCase("student")) {
                user.setCgpa(cgpa);
                user.setBacklogCount(backlogCount != null ? backlogCount : 0);
            }

            // ✅ save + get saved user
            User savedUser = loginService.register(user);

            // ✅ If student, also create the student table entry
            if (role.equalsIgnoreCase("student")) {
                Student student = new Student();
                student.setUser(savedUser);
                student.setName(name);
                student.setEmail(email);
                student.setSrn(srn);
                student.setPhone(phone);
                student.setAddress(address);
                student.setBranch(branch);
                student.setBatchYear(batchYear);
                student.setSgpaSem1(sgpaSem1);
                student.setSgpaSem2(sgpaSem2);
                student.setSgpaSem3(sgpaSem3);
                student.setSgpaSem4(sgpaSem4);
                student.setSgpaSem5(sgpaSem5);
                student.setSgpaSem6(sgpaSem6);
                student.setSgpaSem7(sgpaSem7);
                student.setCgpa(cgpa);
                student.setBacklogCount(backlogCount != null ? backlogCount : 0);
                student.setResumeUrl(resumeUrl);
                student.setIsEligible(false);
                student.setIsPlaced(false);
                studentRepository.save(student);
            }

            // ✅ STORE IN SESSION
            session.setAttribute("loggedInUser", savedUser);

            return "redirect:/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pages/register";
        }
    }
    
}