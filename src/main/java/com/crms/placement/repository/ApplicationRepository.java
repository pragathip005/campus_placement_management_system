package com.crms.placement.repository;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}