package com.crms.placement.repository;

import com.crms.placement.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Optional<Application> findByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);
    long countByOpportunityId(Integer opportunityId);
    long countByOpportunityIdAndStatus(Integer opportunityId, String status);
}
