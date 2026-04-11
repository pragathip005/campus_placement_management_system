package com.crms.placement.service;


import com.crms.placement.model.Application;
import com.crms.placement.model.Opportunity;
import com.crms.placement.model.User;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OpportunityRepository;
import com.crms.placement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpportunityService {

    private final OpportunityRepository repo;
    private final ApplicationRepository applicationRepo;
    private final UserRepository userRepo;

    public OpportunityService(OpportunityRepository repo, ApplicationRepository applicationRepo, UserRepository userRepo) {
        this.repo = repo;
        this.applicationRepo = applicationRepo;
        this.userRepo = userRepo;
    }

    public List<Opportunity> getAllJobs() {
        return repo.findAll();
    }

    public Opportunity getJobById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public boolean isApplied(Integer studentId, Integer opportunityId) {
        return applicationRepo.findByStudentIdAndOpportunityId(studentId, opportunityId).isPresent();
    }

    public boolean isEligible(Integer studentId, Integer opportunityId) {
        Optional<User> userOpt = userRepo.findById((long) studentId);
        Optional<Opportunity> jobOpt = repo.findById(opportunityId);

        if (userOpt.isEmpty() || jobOpt.isEmpty()) {
            return false;
        }

        User student = userOpt.get();
        Opportunity job = jobOpt.get();

        // Check if already applied
        if (isApplied(studentId, opportunityId)) {
            return false;
        }

        // Check CGPA
        if (student.getCgpa() == null || student.getCgpa() < job.getMinCgpa()) {
            return false;
        }

        // Check backlogs
        if (student.getBacklogCount() == null) {
            student.setBacklogCount(0);
        }
        if (student.getBacklogCount() > (job.getMaxBacklogs() != null ? job.getMaxBacklogs() : 0)) {
            return false;
        }

        return true;
    }

    public Application applyToJob(Integer studentId, Integer opportunityId) {
        Application application = new Application(studentId, opportunityId, "APPLIED");
        return applicationRepo.save(application);
    }

    public double calculateShortlistSuccessRate(Integer opportunityId) {
        long total = applicationRepo.countByOpportunityId(opportunityId);
        if (total == 0) return 0;
        long accepted = applicationRepo.countByOpportunityIdAndStatus(opportunityId, "ACCEPTED");
        return (accepted * 100.0) / total;
    }
}

