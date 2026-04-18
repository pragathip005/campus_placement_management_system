package com.crms.placement.controller;

import com.crms.placement.model.Student;
import com.crms.placement.model.User;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.LoginService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.crms.placement.model.Company;
import com.crms.placement.repository.CompanyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final LoginService loginService;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;

    public AuthController(LoginService loginService,StudentRepository studentRepository,CompanyRepository companyRepository) {
        this.loginService = loginService;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
    }

    // show login page
@GetMapping("/login")
public String showLogin(Model model, HttpSession session) {
    // Generate CSRF token
    String csrfToken = UUID.randomUUID().toString();
    session.setAttribute("csrfToken", csrfToken);
    model.addAttribute("csrfToken", csrfToken);
    return "pages/login";
}

// handle login
@PostMapping("/login")
public String login(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String role,
        @RequestParam String csrfToken,
        HttpSession session,
        HttpServletRequest request,
        Model model
) {

    String sessionToken = (String) session.getAttribute("csrfToken");

    if (sessionToken == null || !sessionToken.equals(csrfToken)) {
        model.addAttribute("error", "Invalid request");
        return "pages/login";
    }

    try {
        User user = loginService.login(email, password, role);

        // ❗ DO NOT invalidate session (THIS WAS THE BUG)
        session.setAttribute("loggedInUser", user);
        session.setMaxInactiveInterval(30 * 60);

        // refresh CSRF safely
        session.setAttribute("csrfToken", UUID.randomUUID().toString());

        System.out.println("LOGIN SUCCESS: " + user.getName());

        if (user.getRole().equalsIgnoreCase("hr")) {
            return "redirect:/hr/dashboard";
        } else if (user.getRole().equalsIgnoreCase("student")) {
            return "redirect:/student/dashboard";
        }

        return "redirect:/dashboard";

    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "pages/login";
    }
}

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate(); // ❗ clears everything

        return "redirect:/login";
    }


    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
        model.addAttribute("csrfToken", csrfToken);
        return "pages/register";
    }

    @PostMapping("/register")
public String register(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password,
        @RequestParam String role,
        
        // HR-specific fields
        @RequestParam(required = false) String companyName,
        @RequestParam(required = false) String companyEmail,
        @RequestParam(required = false) String companyPassword,
        @RequestParam(required = false) String industry,
        
        // Student-specific fields
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
        
        @RequestParam String csrfToken,
        HttpSession session,
        Model model
) {
    String sessionToken = (String) session.getAttribute("csrfToken");

    if (sessionToken == null || !sessionToken.equals(csrfToken)) {
        model.addAttribute("error", "Invalid request");
        String newToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", newToken);
        model.addAttribute("csrfToken", newToken);
        return "pages/register";
    }

    try {
        User user = new User();
        
        // ✅ HANDLE HR REGISTRATION
        if (role.equalsIgnoreCase("hr")) {
            
            // Validate HR fields
            if (companyName == null || companyName.trim().isEmpty()) {
                throw new RuntimeException("Company name is required");
            }
            if (companyEmail == null || companyEmail.trim().isEmpty()) {
                throw new RuntimeException("Company email is required");
            }
            if (companyPassword == null || companyPassword.trim().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            if (industry == null || industry.trim().isEmpty()) {
                throw new RuntimeException("Industry is required");
            }
            
            // Use company details for User table
            user.setName(companyName);
            user.setEmail(companyEmail);
            user.setPassword(companyPassword);
            user.setRole("HR");
            
            System.out.println("🏢 [REGISTER] HR User: " + companyEmail);
            
        } 
        // ✅ HANDLE STUDENT REGISTRATION
        else if (role.equalsIgnoreCase("student")) {
            
            // Validate student fields
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            if (cgpa == null) {
                throw new RuntimeException("CGPA is required");
            }
            
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole("STUDENT");
            user.setCgpa(cgpa);
            user.setBacklogCount(backlogCount != null ? backlogCount : 0);
            
            System.out.println("🎓 [REGISTER] Student User: " + email);
        }
        else {
            throw new RuntimeException("Invalid role selected");
        }

        // ✅ Save user (password will be hashed in LoginService)
        User savedUser = loginService.register(user);
        
        System.out.println("✅ [REGISTER] User saved with ID: " + savedUser.getUserId());

        // ✅ Create Company record for HR
        if (role.equalsIgnoreCase("hr")) {
            Company company = new Company();
            company.setUser(savedUser);
            company.setName(companyName);
            company.setEmail(companyEmail);
            company.setIndustry(industry);

            companyRepository.save(company);
            
            System.out.println("✅ [REGISTER] Company saved: " + company.getName());
        }

        // ✅ Create Student record for STUDENT
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
            
            System.out.println("✅ [REGISTER] Student record saved");
        }

        // ✅ Store in session
        session.setAttribute("loggedInUser", savedUser);
        session.setMaxInactiveInterval(30 * 60);

        System.out.println("✅ [REGISTER] Registration complete - redirecting");

        // ✅ Redirect based on role
        if (savedUser.getRole().equalsIgnoreCase("HR")) {
            return "redirect:/hr/dashboard";
        } else if (savedUser.getRole().equalsIgnoreCase("STUDENT")) {
            return "redirect:/student/dashboard";
        }

        return "redirect:/dashboard";

    } catch (Exception e) {
        System.out.println("❌ [REGISTER] ERROR: " + e.getMessage());
        e.printStackTrace();
        
        model.addAttribute("error", e.getMessage());
        String newToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", newToken);
        model.addAttribute("csrfToken", newToken);
        
        return "pages/register";
    }
}    
}