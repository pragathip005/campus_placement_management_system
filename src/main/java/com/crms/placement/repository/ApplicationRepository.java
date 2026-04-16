package com.crms.placement.repository;

import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    // 🔥 FIX: return List instead of Optional
    List<Application> findByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);

    long countByOpportunityId(Integer opportunityId);

    long countByOpportunityIdAndStatus(Integer opportunityId, ApplicationStatus status);
}