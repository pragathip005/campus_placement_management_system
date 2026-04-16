package com.crms.placement.service;

import com.crms.placement.model.Opportunity;
import com.crms.placement.model.Student;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OpportunityRepository;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Component;

@Component
public class EligibilityChecker {

    private final StudentRepository studentRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;

    public EligibilityChecker(StudentRepository studentRepository,
                            OpportunityRepository opportunityRepository,
                            ApplicationRepository applicationRepository) {
        this.studentRepository = studentRepository;
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
    }

    public EligibilityResult checkEligibility(Integer studentId, Integer opportunityId) {
        Student student = studentRepository.findById((long) studentId).orElse(null);
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);

        if (student == null || opportunity == null) {
            return EligibilityResult.ineligible("Student or Opportunity not found");
        }

        if (hasPreviouslyApplied(studentId, opportunityId)) {
            return EligibilityResult.ineligible("Already applied to this opportunity");
        }

        EligibilityResult placementResult = checkPlacementEligibility(student);
        if (!placementResult.isEligible()) return placementResult;

        EligibilityResult cgpaResult = checkCgpaEligibility(student, opportunity);
        if (!cgpaResult.isEligible()) return cgpaResult;

        EligibilityResult backlogResult = checkBacklogEligibility(student, opportunity);
        if (!backlogResult.isEligible()) return backlogResult;

        EligibilityResult branchResult = checkBranchEligibility(student, opportunity);
        if (!branchResult.isEligible()) return branchResult;

        return EligibilityResult.eligible();
    }

    private EligibilityResult checkCgpaEligibility(Student student, Opportunity opportunity) {
        Double studentCgpa = student.getCgpa() != null ? student.getCgpa() : 0.0;
        Double minCgpa = opportunity.getMinCgpa() != null ? opportunity.getMinCgpa() : 0.0;

        if (studentCgpa < minCgpa) {
            return EligibilityResult.ineligible(
                String.format("CGPA %.2f is below minimum requirement of %.2f",
                    studentCgpa, minCgpa)
            );
        }
        return EligibilityResult.eligible();
    }

    private EligibilityResult checkPlacementEligibility(Student student) {
        if (Boolean.TRUE.equals(student.getIsPlaced())) {
            return EligibilityResult.ineligible("Student is already placed");
        }
        return EligibilityResult.eligible();
    }

    private EligibilityResult checkBacklogEligibility(Student student, Opportunity opportunity) {
        Integer studentBacklogs = student.getBacklogCount() != null ?
            student.getBacklogCount() : 0;
        Integer maxBacklogs = opportunity.getMaxBacklogs() != null ?
            opportunity.getMaxBacklogs() : 0;

        if (studentBacklogs > maxBacklogs) {
            return EligibilityResult.ineligible(
                String.format("Backlogs (%d) exceed limit of %d",
                    studentBacklogs, maxBacklogs)
            );
        }
        return EligibilityResult.eligible();
    }

    private EligibilityResult checkBranchEligibility(Student student, Opportunity opportunity) {
        String studentBranch = student.getBranch();
        var eligibleBranches = opportunity.getEligibleBranches();

        // If no branch restrictions, student is eligible
        if (eligibleBranches == null || eligibleBranches.isEmpty()) {
            return EligibilityResult.eligible();
        }

        // Check if student's branch is in the eligible branches list
        if (studentBranch != null && eligibleBranches.contains(studentBranch)) {
            return EligibilityResult.eligible();
        }

        return EligibilityResult.ineligible(
            String.format("Branch %s is not eligible. Eligible branches: %s",
                studentBranch, String.join(", ", eligibleBranches))
        );
    }

    private boolean hasPreviouslyApplied(Integer studentId, Integer opportunityId) {
        return applicationRepository.findByStudentIdAndOpportunityId(studentId, opportunityId)
            .isPresent();
    }
}
