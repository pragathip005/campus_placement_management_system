package com.crms.placement.repository;

import com.crms.placement.model.InterviewExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewExperienceRepository extends JpaRepository<InterviewExperience, Integer> {

    @Query("SELECT ie FROM InterviewExperience ie WHERE ie.company.company_id = :companyId ORDER BY ie.experienceDate DESC")
    List<InterviewExperience> findByCompanyIdOrderByExperienceDateDesc(Integer companyId);

    @Query("SELECT ie FROM InterviewExperience ie ORDER BY ie.company.name ASC, ie.experienceDate DESC")
    List<InterviewExperience> findAllGroupedByCompany();
}
