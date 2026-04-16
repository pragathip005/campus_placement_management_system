package com.crms.placement.repository;

import com.crms.placement.model.OnlineAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlineAssessmentRepository extends JpaRepository<OnlineAssessment, Integer> {

    Optional<OnlineAssessment> findByApplication_ApplicationId(Integer applicationId);
}