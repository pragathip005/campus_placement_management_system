package com.crms.placement.service;

import com.crms.placement.entity.OAPrepHistory;
import com.crms.placement.repository.OAPrepHistoryRepository;
import com.crms.placement.repository.AlumniRepository;
import com.crms.placement.entity.Alumni;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PreparationHubService {

    private final OAPrepHistoryRepository oaPrepHistoryRepository;
    private final AlumniRepository alumniRepository;

    public PreparationHubService(OAPrepHistoryRepository repo,
                                 AlumniRepository alumniRepository) {
        this.oaPrepHistoryRepository = repo;
        this.alumniRepository = alumniRepository;
    }

    // 🔥 helper: get alumni name
    private String getAlumniName(Long id) {
        if (id == null) return "Alumni";

        return alumniRepository.findById(id)
                .map(Alumni::getName)
                .orElse("Alumni");
    }

    /**
     * ✅ ALL OA QUESTIONS
     */
    public List<Map<String, Object>> getAllOAQuestions() {
        return oaPrepHistoryRepository.findAll().stream()
                .filter(e -> e.getQuestions() != null && !e.getQuestions().isEmpty())
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("company", e.getCompany());
                    m.put("questions", e.getQuestions());
                    m.put("oaDifficulty", e.getOaDifficulty());
                    m.put("tips", e.getTips());
                    m.put("alumniName", getAlumniName(e.getAlumniId())); // ✅ FIX
                    return m;
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ ALL INTERVIEW EXPERIENCES
     */
    public List<Map<String, Object>> getAllInterviewExperiences() {
        return oaPrepHistoryRepository.findAll().stream()
                .filter(e -> e.getInterviewExperience() != null && !e.getInterviewExperience().isEmpty())
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("company", e.getCompany());
                    m.put("interviewExperience", e.getInterviewExperience());
                    m.put("oaDifficulty", e.getOaDifficulty());
                    m.put("tips", e.getTips());
                    m.put("alumniName", getAlumniName(e.getAlumniId())); // ✅ FIX
                    return m;
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ FILTER BY COMPANY (OA)
     */
    public List<Map<String, Object>> getOAQuestionsByCompanyName(String companyName) {
        return getAllOAQuestions().stream()
                .filter(e -> e.get("company").toString().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    /**
     * ✅ FILTER BY COMPANY (INTERVIEW)
     */
    public List<Map<String, Object>> getInterviewExperiencesByCompanyName(String companyName) {
        return getAllInterviewExperiences().stream()
                .filter(e -> e.get("company").toString().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    /**
     * ✅ MAIN HUB DATA
     */
    public Map<String, Map<String, Object>> getPreparationHubData() {

        List<Map<String, Object>> allQuestions = getAllOAQuestions();
        List<Map<String, Object>> allExperiences = getAllInterviewExperiences();

        Map<String, Map<String, Object>> result = new LinkedHashMap<>();

        Set<String> companies = new LinkedHashSet<>();

        allQuestions.forEach(q -> companies.add(q.get("company").toString().toLowerCase()));
        allExperiences.forEach(e -> companies.add(e.get("company").toString().toLowerCase()));

        for (String companyKey : companies) {

            String displayName = null;

            List<Map<String, Object>> qList = allQuestions.stream()
                    .filter(q -> q.get("company").toString().equalsIgnoreCase(companyKey))
                    .collect(Collectors.toList());

            List<Map<String, Object>> eList = allExperiences.stream()
                    .filter(e -> e.get("company").toString().equalsIgnoreCase(companyKey))
                    .collect(Collectors.toList());

            if (!qList.isEmpty()) displayName = qList.get(0).get("company").toString();
            else if (!eList.isEmpty()) displayName = eList.get(0).get("company").toString();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", displayName);
            data.put("questions", qList);
            data.put("experiences", eList);

            result.put(companyKey, data);
        }

        return result;
    }
}