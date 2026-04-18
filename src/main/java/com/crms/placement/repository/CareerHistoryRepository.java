package com.crms.placement.repository;

import com.crms.placement.entity.CareerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerHistoryRepository extends JpaRepository<CareerHistory, Long> {
    List<CareerHistory> findByAlumniId(Long alumniId);
    long countByAlumniId(Long alumniId);
}
