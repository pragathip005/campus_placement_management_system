package com.crms.placement.repository;

import com.crms.placement.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepository extends JpaRepository<TestUser, Long> {
}