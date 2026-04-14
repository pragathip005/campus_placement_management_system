package com.crms.placement.service;

import com.crms.placement.model.Opportunity;
import com.crms.placement.model.Student;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OpportunityRepository;
import com.crms.placement.repository.StudentRepository;
import org.springframework.stereotype.Component;

/**
 * GRASP: Information Expert - Expert in eligibility checking
 * SOLID: Single Responsibility - Only checks eligibility
 *
 * Validates if a student is eligible to apply for an opportunity
 * based on CGPA, backlogs, and prior applications.
 */
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

    /**
     * Check if student is eligible for opportunity
     * Returns detailed eligibility result with reason if ineligible
     */
    public EligibilityResult checkEligibility(Integer studentId, Integer opportunityId) {
        Student student = studentRepository.findById((long) studentId).orElse(null);
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);

        if (student == null || opportunity == null) {
            return EligibilityResult.ineligible("Student or Opportunity not found");
        }

        if (hasPreviouslyApplied(studentId, opportunityId)) {
            return EligibilityResult.ineligible("Already applied to this opportunity");
        }

        EligibilityResult cgpaResult = checkCgpaEligibility(student, opportunity);
        if (!cgpaResult.isEligible()) return cgpaResult;

        EligibilityResult backlogResult = checkBacklogEligibility(student, opportunity);
        if (!backlogResult.isEligible()) return backlogResult;

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

    private boolean hasPreviouslyApplied(Integer studentId, Integer opportunityId) {
        return applicationRepository.findByStudentIdAndOpportunityId(studentId, opportunityId)
            .isPresent();
    }
}
