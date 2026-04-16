package com.crms.placement.repository;

import com.crms.placement.model.OAQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OAQuestionRepository extends JpaRepository<OAQuestion, Integer> {

    @Query("SELECT oq FROM OAQuestion oq WHERE oq.company.company_id = :companyId ORDER BY oq.addedDate DESC")
    List<OAQuestion> findByCompanyIdOrderByAddedDateDesc(Integer companyId);

    @Query("SELECT oq FROM OAQuestion oq ORDER BY oq.company.name ASC, oq.addedDate DESC")
    List<OAQuestion> findAllGroupedByCompany();
}
