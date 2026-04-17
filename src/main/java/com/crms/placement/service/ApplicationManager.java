package com.crms.placement.service;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.repository.ApplicationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GRASP: Creator + Information Expert
 * SOLID: Single Responsibility - Only manages applications
 */
@Component
public class ApplicationManager {

    private final ApplicationRepository applicationRepository;

    public ApplicationManager(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Submit a new application
     */
    public Application submitApplication(Integer studentId, Integer opportunityId) {

        // ✅ Prevent duplicate applications (CORRECT PLACE)
        if (hasApplied(studentId, opportunityId)) {
            throw new RuntimeException("Already applied to this opportunity");
        }

        Application application = new Application(
                studentId,
                opportunityId,
                ApplicationStatus.APPLIED
        );

        return applicationRepository.save(application);
    }

    /**
     * Check if student has already applied
     */
    public boolean hasApplied(Integer studentId, Integer opportunityId) {
        return !applicationRepository
                .findByStudentIdAndOpportunityId(studentId, opportunityId)
                .isEmpty();
    }

    /**
     * Get student's application for a specific opportunity
     */
    public Application getApplication(Integer studentId, Integer opportunityId) {

        List<Application> applications =
                applicationRepository.findByStudentIdAndOpportunityId(studentId, opportunityId);

        return applications.isEmpty() ? null : applications.get(0);
    }
}