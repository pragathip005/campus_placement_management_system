package com.crms.placement.repository;

import com.crms.placement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findBySrn(String srn);
    Student findByEmail(String email);
}
