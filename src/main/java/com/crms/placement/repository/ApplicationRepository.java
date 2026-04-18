package com.crms.placement.repository;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    // =====================================================
    // ✅ CORE EXISTING METHODS (FIXED TYPE SAFETY)
    // =====================================================

    Optional<Application> findByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);

    long countByOpportunityId(Integer opportunityId);

    long countByOpportunityIdAndStatus(Integer opportunityId, ApplicationStatus status);

    // =====================================================
    // ✅ STUDENT DASHBOARD QUERIES (YOUR LOGIC PRESERVED)
    // =====================================================

    /** All applications for a student, newest first */
    List<Application> findByStudentIdOrderByAppliedDateDesc(Integer studentId);

    /** Total application count */
    long countByStudentId(Integer studentId);

    /** Count by status (FIXED → ENUM instead of String) */
    long countByStudentIdAndStatus(Integer studentId, ApplicationStatus status);

    /** OA pending count */
    @Query("""
        SELECT COUNT(a) FROM Application a
        WHERE a.studentId = :studentId
        AND a.status = com.crms.placement.model.ApplicationStatus.OA_SENT
    """)
    long countOaPendingByStudentId(@Param("studentId") Integer studentId);

    /** Shortlisted pipeline count */
    @Query("""
        SELECT COUNT(a) FROM Application a
        WHERE a.studentId = :studentId
        AND a.status IN (
            com.crms.placement.model.ApplicationStatus.SHORTLISTED,
            com.crms.placement.model.ApplicationStatus.INTERVIEW_SCHEDULED,
            com.crms.placement.model.ApplicationStatus.INTERVIEW_DONE,
            com.crms.placement.model.ApplicationStatus.OFFERED,
            com.crms.placement.model.ApplicationStatus.ACCEPTED
        )
    """)
    long countShortlistedByStudentId(@Param("studentId") Integer studentId);

    // =====================================================
    // 🔥 HR DISPATCH / JOB SIDE LOGIC (FROM INCOMING FILE)
    // =====================================================

    /** Get ALL applications for a job */
    List<Application> findByOpportunityId(Integer opportunityId);

    /** Filter applications by status (for HR workflow) */
    List<Application> findByOpportunityIdAndStatus(Integer opportunityId, ApplicationStatus status);

}