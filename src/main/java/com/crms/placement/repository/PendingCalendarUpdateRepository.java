package com.crms.placement.repository;

import com.crms.placement.model.PendingCalendarUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingCalendarUpdateRepository extends JpaRepository<PendingCalendarUpdate, Long> {

    /** Returns all undelivered updates for a student — used by the polling endpoint. */
    List<PendingCalendarUpdate> findByStudentIdAndDeliveredFalse(Long studentId);
}
