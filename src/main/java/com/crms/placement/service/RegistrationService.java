package com.crms.placement.service;

import com.crms.placement.dto.RegistrationRequestDTO;
import com.crms.placement.model.User;
import com.crms.placement.model.Student;
import com.crms.placement.model.Company;
import com.crms.placement.entity.Alumni;
import com.crms.placement.repository.UserRepository;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.AlumniRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.crms.placement.service.SupabaseService;


@Service
public class RegistrationService {

    private final SupabaseService supabaseService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final AlumniRepository alumniRepository;

    public RegistrationService(UserRepository userRepository,
                               StudentRepository studentRepository,
                               CompanyRepository companyRepository,
                               AlumniRepository alumniRepository,
                               SupabaseService supabaseService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.alumniRepository = alumniRepository;
        this.supabaseService = supabaseService;
    }

    @Transactional
    public User register(RegistrationRequestDTO request, MultipartFile resumeFile) {
        // 1. Create User
        User user = new User();
        user.setRole(request.getRole().toUpperCase());
        
        if ("HR".equalsIgnoreCase(request.getRole())) {
            user.setName(request.getCompanyName());
            user.setEmail(request.getCompanyEmail());
            user.setPassword(request.getCompanyPassword());
        } else {
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
        }

        user = userRepository.save(user);
        Long userId = user.getUserId();

        // 2. Create role-specific entity
        if ("STUDENT".equalsIgnoreCase(request.getRole())) {
            Student student = new Student();
            student.setStudentId(userId);
            student.setName(request.getName());
            student.setEmail(request.getEmail());
            student.setSrn(request.getSrn());
            student.setBranch(request.getBranch());
            student.setBatchYear(request.getBatchYear());
            student.setCgpa(request.getCgpa());
            student.setBacklogCount(request.getBacklogCount());
            student.setSgpaSem1(request.getSgpaSem1());
            student.setSgpaSem2(request.getSgpaSem2());
            student.setSgpaSem3(request.getSgpaSem3());
            student.setSgpaSem4(request.getSgpaSem4());
            student.setSgpaSem5(request.getSgpaSem5());
            student.setSgpaSem6(request.getSgpaSem6());
            student.setSgpaSem7(request.getSgpaSem7());
            student.setPhone(request.getPhone());
            student.setAddress(request.getAddress());
            String resumeUrl = null;
                if (resumeFile != null && !resumeFile.isEmpty()) {
                    resumeUrl = supabaseService.uploadResume(resumeFile);
                }
            student.setResumeUrl(resumeUrl);
            student.setIsEligible(true); // Default
            student.setIsPlaced(false); // Default
            studentRepository.save(student);
        } else if ("HR".equalsIgnoreCase(request.getRole())) {
            Company company = new Company();
            company.setCompany_id(userId.intValue());
            company.setName(request.getCompanyName());
            company.setEmail(request.getCompanyEmail());
            company.setIndustry(request.getIndustry());
            companyRepository.save(company);
        } else if ("ALUMNI".equalsIgnoreCase(request.getRole())) {
            Alumni alumni = new Alumni();
            alumni.setId(userId);
            alumni.setName(request.getName());
            alumni.setEmail(request.getEmail());
            alumni.setPhone(request.getPhone());
            alumni.setGraduationYear(request.getGraduationYear());
            alumni.setCurrentCompany(request.getCurrentCompany());
            alumni.setCurrentJobRole(request.getCurrentJobRole());
            alumni.setPlacementStatus(request.getPlacementStatus());
            alumni.setLinkedinUrl(request.getLinkedinUrl());
            // companyHistory is a textarea in the form, let's see if Alumni has a field for it
            // Based on Alumni.java, there isn't a direct field for companyHistory, maybe higherStudies?
            // I'll skip it for now or check if I should add it.
            alumniRepository.save(alumni);
        }

        return user;
    }
}
