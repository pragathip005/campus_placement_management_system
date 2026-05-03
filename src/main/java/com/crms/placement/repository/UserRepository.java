package com.crms.placement.repository;
import java.util.Optional;
import com.crms.placement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndRole(String email, String role);
}