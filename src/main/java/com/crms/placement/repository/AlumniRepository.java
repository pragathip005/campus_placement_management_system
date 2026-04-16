package com.crms.placement.repository;

import com.crms.placement.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Long> {

    Alumni findByEmail(String email);

    long countByCurrentCompanyIgnoreCase(String companyName);

    List<Alumni> findByCurrentCompanyIgnoreCase(String companyName);
}
