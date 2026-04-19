package com.crms.placement.service;

import com.crms.placement.dto.ApplicationDashboardDto;
import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;

import com.crms.placement.model.Company;
import com.crms.placement.model.Opportunity;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OpportunityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentDashboardService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;

    public StudentDashboardService(ApplicationRepository applicationRepository,
                                   OpportunityRepository opportunityRepository) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
    }

    /**
     * Build dashboard DTOs for the given student.
     * Only 2 bulk queries total — no N+1.
     *
     * NOTE: Company is already eagerly reachable via opp.getCompany()
     * because Opportunity has @ManyToOne Company company.
     * Spring Data will join it in the same query via the FK.
     */
    public List<ApplicationDashboardDto> getDashboardApplications(Integer studentId) {

        // 1. All applications for this student, newest first
        List<Application> apps =
                applicationRepository.findByStudentIdOrderByAppliedDateDesc(studentId);

        if (apps.isEmpty()) return List.of();

        // 2. Bulk-fetch all referenced opportunities (includes Company via @ManyToOne)
        List<Integer> oppIds = apps.stream()
                .map(Application::getOpportunityId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Opportunity> oppMap = opportunityRepository.findAllById(oppIds)
                .stream()
                .collect(Collectors.toMap(Opportunity::getOpportunity_id, o -> o));

        // 3. Assemble DTOs
        return apps.stream().map(app -> {
            ApplicationDashboardDto dto = new ApplicationDashboardDto();
            dto.setApplicationId(app.getApplicationId());
            dto.setStatus(app.getStatus());
            dto.setAppliedDate(app.getAppliedDate());

            Opportunity opp = oppMap.get(app.getOpportunityId());
            if (opp != null) {
                dto.setOpportunityName(opp.getName());   // Opportunity.name
                dto.setOpportunityRole(opp.getRole());
                dto.setCtc(opp.getCtc());
                dto.setStipend(opp.getStipend());
                dto.setType(opp.getType());

                // Company is @ManyToOne on Opportunity — field: opp.getCompany()
                Company company = opp.getCompany();
                if (company != null) {
                    dto.setCompanyName(company.getName()); // Company.name
                }
            }

            // ── Timeline mapping ──────────────────────────────────
            // APPLIED → OA_SENT → OA_COMPLETED → INTERVIEW → SELECTED → OFFER OUTCOME

            String status = app.getStatus() == null ? "" : app.getStatus().name();
            int idx;
            switch (status) {
                case "APPLIED" -> idx = 0;
                case "OA_SENT" -> idx = 1;
                case "OA_COMPLETED" -> idx = 2;
                case "INTERVIEW" -> idx = 3;
                case "SELECTED" -> idx = 4;
                case "OFFER_ACCEPTED", "OFFER_REJECTED", "REJECTED" -> idx = 5;
                default -> idx = 0;
            }
            dto.setStatusIndex(idx);
            dto.setProgressPercent((idx * 100) / 5);

            return dto;
        }).collect(Collectors.toList());
    }

    /** Total applications */
    public long getTotalApplications(Integer studentId) {
        return applicationRepository.countByStudentId(studentId);
    }

    /** Shortlisted = ACCEPTED + OFFERED */
    public long getShortlistedCount(Integer studentId) {
        return applicationRepository.countByStudentIdAndStatusIn(
        studentId,
        List.of(
            ApplicationStatus.INTERVIEW,
            ApplicationStatus.SELECTED,
            ApplicationStatus.OFFER_ACCEPTED
        )
    );
}

    /** OA Pending = APPLIED (update if you add OA_SENT status later) */
    public long getOaPendingCount(Integer studentId) {
        return applicationRepository.countByStudentIdAndStatusIn(
        studentId,
        List.of(
            ApplicationStatus.APPLIED,
            ApplicationStatus.OA_SENT
        )
    );
    }
}