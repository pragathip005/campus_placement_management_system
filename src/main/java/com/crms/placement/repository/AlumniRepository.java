package com.crms.placement.repository;

import com.crms.placement.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Long> {
}
