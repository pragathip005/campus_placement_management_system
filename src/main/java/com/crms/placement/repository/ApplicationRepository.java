package com.crms.placement.repository;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    // Existing methods (keep as-is)
    List<Application> findByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);

    long countByOpportunityId(Integer opportunityId);

    long countByOpportunityIdAndStatus(Integer opportunityId, ApplicationStatus status);

    // 🔥 NEW (for 3.2 dispatch logic)

    // Get ALL applications for a job
    List<Application> findByOpportunityId(Integer opportunityId);

    // ✅ BEST METHOD (use this for dispatch)
    // Get only students who are still in APPLIED state
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
}