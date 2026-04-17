package com.crms.placement.repository;

import com.crms.placement.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    // ── Your existing methods (unchanged) ────────────────────────
    Optional<Application> findByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);
    long countByOpportunityId(Integer opportunityId);
    long countByOpportunityIdAndStatus(Integer opportunityId, String status);

    // ── New: dashboard queries ────────────────────────────────────

    /** All applications for a student, newest first */
    List<Application> findByStudentIdOrderByAppliedDateDesc(Integer studentId);

    /** Total application count for a student */
    long countByStudentId(Integer studentId);

    /** Count by student + specific status string */
    long countByStudentIdAndStatus(Integer studentId, String status);

    /** Count shortlisted = ACCEPTED + OFFERED */
    @Query("SELECT COUNT(a) FROM Application a " +
           "WHERE a.studentId = :studentId AND a.status IN ('ACCEPTED', 'OFFERED')")
    long countShortlistedByStudentId(@Param("studentId") Integer studentId);
}