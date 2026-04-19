package com.crms.placement.repository;

import com.crms.placement.model.InterviewSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long> {

    // Find a student's assigned slot (a student can only have one slot across all companies)
    Optional<InterviewSlot> findByStudent_StudentId(Long studentId);

    // Opportunity's PK field is named opportunity_id (with underscore), which conflicts with
    // Spring Data's _ path separator — use explicit JPQL to avoid ambiguity.
    @Query("SELECT s FROM InterviewSlot s WHERE s.opportunity.opportunity_id = :opportunityId")
    List<InterviewSlot> findByOpportunityId(@Param("opportunityId") Integer opportunityId);

    // Calendar: fetch all slots for a student with opportunity + company eager-loaded.
    // Avoids LazyInitializationException when CalendarController reads slot.getOpportunity().getCompany().
    @Query("SELECT s FROM InterviewSlot s JOIN FETCH s.opportunity o JOIN FETCH o.company WHERE s.student.studentId = :studentId")
    List<InterviewSlot> findByStudentIdWithDetails(@Param("studentId") Long studentId);
}
