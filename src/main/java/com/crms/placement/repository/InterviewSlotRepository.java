package com.crms.placement.repository;

import com.crms.placement.model.InterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long> {

    // Find a student's assigned slot (a student can only have one slot across all companies)
    Optional<InterviewSlot> findByStudent_StudentId(Long studentId);

    // Find all slots for a given opportunity (useful for admin views)
    List<InterviewSlot> findByOpportunity_Opportunity_id(Integer opportunityId);
}
