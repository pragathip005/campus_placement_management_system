package com.crms.placement.service;

import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.dto.ApplicationStatisticsDTO;
import org.springframework.stereotype.Component;

@Component
public class OpportunityStatistics {

    private final ApplicationRepository applicationRepository;

    public OpportunityStatistics(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public double calculateShortlistSuccessRate(Integer opportunityId) {
        long total = applicationRepository.countByOpportunityId(opportunityId);
        if (total == 0) return 0;

        long selected = applicationRepository.countByOpportunityIdAndStatus(
                opportunityId, ApplicationStatus.SHORTLISTED
);

        return (selected * 100.0) / total;
    }

    public ApplicationStatisticsDTO getApplicationStatistics(Integer opportunityId) {

        long total = applicationRepository.countByOpportunityId(opportunityId);

        long selected = applicationRepository.countByOpportunityIdAndStatus(
                opportunityId, ApplicationStatus.SHORTLISTED
        );

        long offerAccepted = applicationRepository.countByOpportunityIdAndStatus(
                opportunityId, ApplicationStatus.ACCEPTED
        );

        long offerRejected = applicationRepository.countByOpportunityIdAndStatus(
                opportunityId, ApplicationStatus.REJECTED
        );

        long rejected = applicationRepository.countByOpportunityIdAndStatus(
                opportunityId, ApplicationStatus.REJECTED
        );

        long pending =
                applicationRepository.countByOpportunityIdAndStatus(opportunityId, ApplicationStatus.APPLIED)
        + applicationRepository.countByOpportunityIdAndStatus(opportunityId, ApplicationStatus.ELIGIBLE)
        + applicationRepository.countByOpportunityIdAndStatus(opportunityId, ApplicationStatus.OA_SENT)
        + applicationRepository.countByOpportunityIdAndStatus(opportunityId, ApplicationStatus.TEST_COMPLETED)
        + applicationRepository.countByOpportunityIdAndStatus(opportunityId, ApplicationStatus.INTERVIEW_SCHEDULED)
        + applicationRepository.countByOpportunityIdAndStatus(opportunityId, ApplicationStatus.INTERVIEW_DONE);

        return new ApplicationStatisticsDTO(
                total,
                pending,
                selected,
                offerAccepted,
                offerRejected,
                rejected
        );
    }
}