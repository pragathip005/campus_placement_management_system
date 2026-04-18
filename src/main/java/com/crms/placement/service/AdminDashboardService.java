package com.crms.placement.service;

import com.crms.placement.repository.AlumniRepository;
import com.crms.placement.repository.CompanyRepository;
import com.crms.placement.repository.OpportunityRepository;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final StudentRepository studentRepository;
    private final AlumniRepository alumniRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;

    public AdminDashboardService(StudentRepository studentRepository,
                                 AlumniRepository alumniRepository,
                                 CompanyRepository companyRepository,
                                 OpportunityRepository opportunityRepository) {
        this.studentRepository = studentRepository;
        this.alumniRepository = alumniRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
    }

    /** Total students */
    public long getTotalStudents() {
        return studentRepository.count();
    }

    /** Total alumni */
    public long getTotalAlumni() {
        return alumniRepository.count();
    }

    /** Total companies */
    public long getTotalCompanies() {
        return companyRepository.count();
    }

    /** Total opportunities */
    public long getTotalOpportunities() {
        return opportunityRepository.count();
    }
}