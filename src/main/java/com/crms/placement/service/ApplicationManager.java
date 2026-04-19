package com.crms.placement.service;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.OnlineAssessment;
import com.crms.placement.model.Opportunity;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OnlineAssessmentRepository;
import com.crms.placement.repository.OpportunityRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationManager {

    private final ApplicationRepository applicationRepository;
    private final OnlineAssessmentRepository onlineAssessmentRepository;
    private final OpportunityRepository opportunityRepository;

    public ApplicationManager(ApplicationRepository applicationRepository,
                              OnlineAssessmentRepository onlineAssessmentRepository,
                              OpportunityRepository opportunityRepository) {
        this.applicationRepository = applicationRepository;
        this.onlineAssessmentRepository = onlineAssessmentRepository;
        this.opportunityRepository = opportunityRepository;
    }

    public Application submitApplication(Integer studentId, Integer opportunityId) {

        // ✅ Duplicate check (same as 3.2)
        if (hasApplied(studentId, opportunityId)) {
            throw new RuntimeException("Already applied to this opportunity");
        }

        // ✅ Fetch opportunity
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new RuntimeException("Opportunity not found"));

        // ✅ Save application as APPLIED
        Application application = new Application(
                studentId,
                opportunityId,
                ApplicationStatus.APPLIED
        );
        application = applicationRepository.save(application);

        // ✅ Create OA only if opportunity has an OA round
        if (Boolean.TRUE.equals(opportunity.getHasOa()) && opportunity.getOaDate() != null) {
            OnlineAssessment oa = new OnlineAssessment();
            oa.setApplication(application);
            oa.setOaLink("https://oa-platform.com/test/" + application.getApplicationId());
            oa.setCompleted(false);
            oa.setNotified(false);
            oa.setScheduledAt(opportunity.getOaDate());
            onlineAssessmentRepository.save(oa);

            application.setStatus(ApplicationStatus.OA_SENT);
            applicationRepository.save(application);

            System.out.println("✅ OA created for student " + studentId
                    + " scheduledAt=" + opportunity.getOaDate());
        } else {
            System.out.println("✅ No OA round for opportunity " + opportunityId
                    + ", status stays APPLIED");
        }

        return application;
    }

    // ✅ Duplicate check
    public boolean hasApplied(Integer studentId, Integer opportunityId) {
    return applicationRepository
            .findByStudentIdAndOpportunityId(studentId, opportunityId)
            .isPresent();
}

    // ✅ Get specific application
    public Application getApplication(Integer studentId, Integer opportunityId) {
    return applicationRepository
            .findByStudentIdAndOpportunityId(studentId, opportunityId)
            .orElse(null);
}

    /**
     * Get all applications for a specific opportunity (HR Use Case)
     */
    public List<Application> getApplicationsByOpportunityId(Integer opportunityId) {
        return applicationRepository.findByOpportunityId(opportunityId);
    }

    /**
     * Update application status (HR Use Case)
     */
    public void updateApplicationStatus(Integer applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(status);
        applicationRepository.save(application);
    }
}