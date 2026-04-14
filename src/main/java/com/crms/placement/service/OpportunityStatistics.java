package com.crms.placement.service;

import com.crms.placement.repository.ApplicationRepository;
import org.springframework.stereotype.Component;

/**
 * GRASP: Information Expert
 * SOLID: Single Responsibility - Only calculates statistics
 *
 * Responsible for all opportunity statistics calculations
 */
@Component
public class OpportunityStatistics {

    private final ApplicationRepository applicationRepository;

    public OpportunityStatistics(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Calculate shortlist success rate for an opportunity
     * Success Rate = (Accepted Applications / Total Applications) * 100
     */
    public double calculateShortlistSuccessRate(Integer opportunityId) {
        long total = applicationRepository.countByOpportunityId(opportunityId);
        if (total == 0) {
            return 0;
        }

        long accepted = applicationRepository.countByOpportunityIdAndStatus(opportunityId, "ACCEPTED");
        return (accepted * 100.0) / total;
    }

    /**
     * Get detailed statistics for an opportunity
     */
    public ApplicationStatisticsDTO getApplicationStatistics(Integer opportunityId) {
        long total = applicationRepository.countByOpportunityId(opportunityId);
        long accepted = applicationRepository.countByOpportunityIdAndStatus(opportunityId, "ACCEPTED");
        long rejected = applicationRepository.countByOpportunityIdAndStatus(opportunityId, "REJECTED");
        long pending = total - accepted - rejected;

        return new ApplicationStatisticsDTO(total, accepted, rejected, pending);
    }
}
