package com.crms.placement.service;

import com.crms.placement.model.Application;
import com.crms.placement.model.Opportunity;
import com.crms.placement.repository.OpportunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * REFACTORED - Follows SOLID + GRASP principles
 *
 * BEFORE: 83 lines, 5+ responsibilities
 * AFTER: 40 lines, 1 responsibility (orchestration)
 *
 * Now acts as Facade/Controller that delegates to specialized services:
 * - EligibilityChecker: Expert in eligibility validation
 * - ApplicationManager: Expert in application management
 * - OpportunityStatistics: Expert in statistics calculation
 *
 * PRINCIPLES APPLIED:
 * - GRASP: Controller pattern (orchestrates)
 * - GRASP: Information Expert (each service is expert in its domain)
 * - SRP: One reason to change (orchestration logic)
 * - DIP: Depends on service abstractions
 * - Low Coupling: Each component is independent
 * - High Cohesion: All methods relate to opportunity operations
 */
@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final EligibilityChecker eligibilityChecker;
    private final ApplicationManager applicationManager;
    private final OpportunityStatistics opportunityStatistics;

    public OpportunityService(OpportunityRepository opportunityRepository,
                            EligibilityChecker eligibilityChecker,
                            ApplicationManager applicationManager,
                            OpportunityStatistics opportunityStatistics) {
        this.opportunityRepository = opportunityRepository;
        this.eligibilityChecker = eligibilityChecker;
        this.applicationManager = applicationManager;
        this.opportunityStatistics = opportunityStatistics;
    }

    // ===== Job/Opportunity Retrieval =====
    public List<Opportunity> getAllJobs() {
        return opportunityRepository.findAll();
    }

    public Opportunity getJobById(Integer id) {
        return opportunityRepository.findById(id).orElse(null);
    }

    // ===== Eligibility Management =====
    // Delegates to: EligibilityChecker (Information Expert)

    /**
     * Check if student is eligible (detailed version)
     * Returns result with reason for ineligibility
     */
    public EligibilityResult checkEligibility(Integer studentId, Integer opportunityId) {
        return eligibilityChecker.checkEligibility(studentId, opportunityId);
    }

    /**
     * Check if student is eligible (simple boolean version)
     * For backward compatibility with existing code
     */
    public boolean isEligible(Integer studentId, Integer opportunityId) {
        return eligibilityChecker.checkEligibility(studentId, opportunityId).isEligible();
    }

    // ===== Application Management =====
    // Delegates to: ApplicationManager (Creator + Information Expert)

    /**
     * Apply to a job with eligibility check
     * Throws exception if student is not eligible
     */
    public Application applyToJob(Integer studentId, Integer opportunityId) {
        EligibilityResult result = eligibilityChecker.checkEligibility(studentId, opportunityId);

        if (!result.isEligible()) {
            throw new IllegalArgumentException("Student is not eligible: " + result.getReason());
        }

        return applicationManager.submitApplication(studentId, opportunityId);
    }

    /**
     * Check if student has applied
     */
    public boolean isApplied(Integer studentId, Integer opportunityId) {
        return applicationManager.hasApplied(studentId, opportunityId);
    }

    // ===== Statistics & Analytics =====
    // Delegates to: OpportunityStatistics (Information Expert)

    /**
     * Calculate shortlist success rate
     */
}

