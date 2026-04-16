package com.crms.placement.repository;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    Optional<Application> findByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);

    long countByOpportunityId(Integer opportunityId);

    // ✅ FIXED (String → ApplicationStatus)
    long countByOpportunityIdAndStatus(Integer opportunityId, ApplicationStatus status);
}