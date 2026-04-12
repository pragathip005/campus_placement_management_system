package com.crms.placement.repository;

import com.crms.placement.entity.OAPrepHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OAPrepHistoryRepository extends JpaRepository<OAPrepHistory, Long> {
    List<OAPrepHistory> findByAlumniId(Long alumniId);
}
