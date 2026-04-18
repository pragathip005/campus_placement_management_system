package com.crms.placement.service;
import org.springframework.transaction.annotation.Transactional;
import com.crms.placement.dto.OADispatchResponseDTO;
import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.OnlineAssessment;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OnlineAssessmentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class OADispatchService {

    private final ApplicationRepository applicationRepository;
    private final OnlineAssessmentRepository onlineAssessmentRepository;
    private final EligibilityChecker eligibilityChecker;

    public OADispatchService(ApplicationRepository applicationRepository,
                             OnlineAssessmentRepository onlineAssessmentRepository,
                             EligibilityChecker eligibilityChecker) {
        this.applicationRepository = applicationRepository;
        this.onlineAssessmentRepository = onlineAssessmentRepository;
        this.eligibilityChecker = eligibilityChecker;
    }

    /**
     * Dispatch OA to eligible APPLIED students for a given opportunity
     */
    @Transactional
    public OADispatchResponseDTO dispatchOA(Integer opportunityId) {

        List<Application> applications =
                applicationRepository.findByOpportunityIdAndStatus(
                        opportunityId,
                        ApplicationStatus.APPLIED
                );

        int total = applications.size();
        int eligibleCount = 0;
        int dispatched = 0;

        for (Application application : applications) {

            // 🔥 Eligibility check
            EligibilityResult result = eligibilityChecker.checkEligibility(
                    application.getStudentId(),
                    application.getOpportunityId()
            );

            if (!result.isEligible()) {
                System.out.println("Skipped studentId=" + application.getStudentId()
                        + " Reason: " + result.getReason());
                continue;
            }

            eligibleCount++;

            // 🚫 Already has OA
            if (application.getOnlineAssessment() != null) {
                System.out.println("Skipped studentId=" + application.getStudentId()
                        + " Reason: OA already assigned");
                continue;
            }

            // ✅ Create OA
            OnlineAssessment oa = new OnlineAssessment();
            oa.setApplication(application);
            oa.setOaLink("https://oa-platform.com/test/" + application.getApplicationId());
            oa.setCompleted(false);
            

            onlineAssessmentRepository.save(oa);

            // ✅ Update status
            application.setStatus(ApplicationStatus.OA_SENT);
            applicationRepository.save(application);

            System.out.println("OA sent to studentId=" + application.getStudentId());

            dispatched++;
        }

        int skipped = total - dispatched;

        return new OADispatchResponseDTO(
                total,
                eligibleCount,
                dispatched,
                skipped
        );
    }
}