package com.crms.placement.repository;

import com.crms.placement.model.AlumniUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniUserRepository extends JpaRepository<AlumniUser, Long> {
    AlumniUser findByEmail(String email);
}