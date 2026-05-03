package com.crms.placement.repository;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.Company;
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
    long countByStudentIdAndStatusIn(Integer studentId, List<ApplicationStatus> statuses);

    // =====================================================
    // 🔥 HR DISPATCH / JOB SIDE LOGIC (FROM INCOMING FILE)
    // =====================================================

    /** Get ALL applications for a job */
    List<Application> findByOpportunityId(Integer opportunityId);

    /** Filter applications by status (for HR workflow) */
    List<Application> findByOpportunityIdAndStatus(Integer opportunityId, ApplicationStatus status);

    // Depends on T3's ApplicationRepository — fetches INTERVIEW applications for a given company,
    // traversing Application -> Opportunity -> Company.
    @Query("SELECT a FROM Application a WHERE a.opportunity.company = :company AND a.status = :status")
    List<Application> findByCompanyAndStatus(@Param("company") Company company,
                                              @Param("status") ApplicationStatus status);

    // Count all applications with a given status (used by StatisticsService)
    long countByStatus(ApplicationStatus status);

    // Eager-fetch opportunity + company for statistics aggregation (avoids N+1 queries)
    @Query("SELECT a FROM Application a JOIN FETCH a.opportunity o JOIN FETCH o.company WHERE a.status = :status")
    List<Application> findByStatusWithOpportunityAndCompany(@Param("status") ApplicationStatus status);

    // Calendar: fetch a student's OA applications with opportunity + company eager-loaded
    // Used by CalendarController to build OA calendar events (oaDate lives on Opportunity).
    @Query("SELECT a FROM Application a JOIN FETCH a.opportunity o JOIN FETCH o.company WHERE a.studentId = :studentId AND a.status IN :statuses")
    List<Application> findByStudentIdAndStatusIn(@Param("studentId") Integer studentId,
                                                 @Param("statuses") List<ApplicationStatus> statuses);

    // Calendar polling: fetch a single application by ID with opportunity + company eager-loaded.
    // Used by CalendarController.buildEventFromUpdate() when delivering OA_ADDED updates.
    @Query("SELECT a FROM Application a JOIN FETCH a.opportunity o JOIN FETCH o.company WHERE a.applicationId = :id")
    Optional<Application> findByApplicationIdWithDetails(@Param("id") Integer id);
}