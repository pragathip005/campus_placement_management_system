package com.crms.placement.service;

import com.crms.placement.model.Application;
import com.crms.placement.repository.ApplicationRepository;
import org.springframework.stereotype.Component;
import com.crms.placement.model.ApplicationStatus;

/**
 * GRASP: Creator + Information Expert
 * SOLID: Single Responsibility - Only manages applications
 *
 * Responsible for:
 * - Creating new applications
 * - Checking application status
 * - Application-related queries
 */
@Component
public class ApplicationManager {

    private final ApplicationRepository applicationRepository;

    public ApplicationManager(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Submit a new application
     * GRASP: Creator - Responsible for creating Application objects
     */
    public Application submitApplication(Integer studentId, Integer opportunityId) {
        Application application = new Application(studentId, opportunityId, ApplicationStatus.APPLIED);
        return applicationRepository.save(application);
    }

    /**
     * Check if student has already applied
     */
    public boolean hasApplied(Integer studentId, Integer opportunityId) {
        return applicationRepository.findByStudentIdAndOpportunityId(studentId, opportunityId)
            .isPresent();
    }

    /**
     * Get student's application for a specific opportunity
     */
    public Application getApplication(Integer studentId, Integer opportunityId) {
        return applicationRepository.findByStudentIdAndOpportunityId(studentId, opportunityId)
            .orElse(null);
    }
}
