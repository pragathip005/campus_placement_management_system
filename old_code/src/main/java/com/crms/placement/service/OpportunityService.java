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

/**
 * ORIGINAL CODE - Before Design Principles Refactoring
 * =====================================================
 *
 * VIOLATIONS:
 * ❌ Single Responsibility Principle (SRP)
 *    - Handles 5+ different responsibilities
 *    - Mixed concerns in one class
 *
 * ❌ Open/Closed Principle (OCP)
 *    - Must modify this class to add new eligibility rules
 *    - Not open for extension, closed for modification
 *
 * ❌ GRASP Information Expert
 *    - Not expert in any single domain
 *    - Tries to handle eligibility, applications, and statistics
 *
 * ❌ Low Coupling
 *    - Tightly coupled to 3 repositories
 *    - Hard to test in isolation
 *
 * PROBLEMS:
 * - 83 lines with mixed logic
 * - isEligible() method is 20+ lines with nested logic
 * - Hard to test (need to mock 3 repos for 1 test)
 * - Hard to extend (new eligibility rule = modify this class)
 * - Hard to maintain (where's the eligibility logic exactly?)
 * - Code duplication across methods
 */
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

    /**
     * ❌ PROBLEMATIC METHOD - 20+ lines, mixed concerns
     *
     * This method violates SRP by:
     * 1. Fetching user data
     * 2. Fetching opportunity data
     * 3. Checking if already applied
     * 4. Validating CGPA
     * 5. Validating backlogs
     * 6. Modifying user state (setBacklogCount)
     *
     * Problems:
     * - Can't return detailed reason for ineligibility
     * - Can't test JUST the CGPA check
     * - Can't test JUST the backlog check
     * - Hard to add new eligibility criteria (modify this method)
     * - Side effect: modifies user object
     */
    public boolean isEligible(Integer studentId, Integer opportunityId) {
        Optional<User> userOpt = userRepo.findById((long) studentId);
        Optional<Opportunity> jobOpt = repo.findById(opportunityId);

        if (userOpt.isEmpty() || jobOpt.isEmpty()) {
            return false;  // ← Lost information: WHY ineligible?
        }

        User student = userOpt.get();
        Opportunity job = jobOpt.get();

        // Check if already applied
        if (isApplied(studentId, opportunityId)) {
            return false;  // ← Lost information
        }

        // Check CGPA
        if (student.getCgpa() == null || student.getCgpa() < job.getMinCgpa()) {
            return false;  // ← Lost information
        }

        // Check backlogs
        if (student.getBacklogCount() == null) {
            student.setBacklogCount(0);  // ← SIDE EFFECT: Modifying user
        }
        if (student.getBacklogCount() > (job.getMaxBacklogs() != null ? job.getMaxBacklogs() : 0)) {
            return false;  // ← Lost information
        }

        return true;
    }

    /**
     * ❌ PROBLEMATIC METHOD - No eligibility check
     *
     * Creates application WITHOUT checking eligibility first
     * This logic mixing happens in the controller (JobController)
     * which violates separation of concerns
     */
    public Application applyToJob(Integer studentId, Integer opportunityId) {
        Application application = new Application(studentId, opportunityId, "APPLIED");
        return applicationRepo.save(application);
    }

    /**
     * ❌ SINGLE PURPOSE METHOD - Statistics calculation mixed with Job service
     *
     * This works but mixes concerns:
     * - Job/Opportunity service shouldn't handle statistics
     * - Statistics logic not reusable elsewhere
     */
    public double calculateShortlistSuccessRate(Integer opportunityId) {
        long total = applicationRepo.countByOpportunityId(opportunityId);
        if (total == 0) return 0;
        long accepted = applicationRepo.countByOpportunityIdAndStatus(opportunityId, "ACCEPTED");
        return (accepted * 100.0) / total;
    }
}
