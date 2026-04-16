package com.crms.placement.service;

import com.crms.placement.model.OAQuestion;
import com.crms.placement.model.InterviewExperience;
import com.crms.placement.repository.OAQuestionRepository;
import com.crms.placement.repository.InterviewExperienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class PreparationHubService {

    private final OAQuestionRepository oaQuestionRepository;
    private final InterviewExperienceRepository interviewExperienceRepository;

    public PreparationHubService(OAQuestionRepository oaQuestionRepository,
                                  InterviewExperienceRepository interviewExperienceRepository) {
        this.oaQuestionRepository = oaQuestionRepository;
        this.interviewExperienceRepository = interviewExperienceRepository;
    }

    /**
     * Get all OA Questions grouped by company
     */
    public List<OAQuestion> getAllOAQuestions() {
        return oaQuestionRepository.findAllGroupedByCompany();
    }

    /**
     * Get all Interview Experiences grouped by company
     */
    public List<InterviewExperience> getAllInterviewExperiences() {
        return interviewExperienceRepository.findAllGroupedByCompany();
    }

    /**
     * Get OA Questions for a specific company by ID
     */
    public List<OAQuestion> getOAQuestionsByCompany(Integer companyId) {
        return oaQuestionRepository.findByCompanyIdOrderByAddedDateDesc(companyId);
    }

    /**
     * Get OA Questions for a specific company by name
     */
    public List<OAQuestion> getOAQuestionsByCompanyName(String companyName) {
        return getAllOAQuestions().stream()
                .filter(q -> q.getCompany().getName().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    /**
     * Get Interview Experiences for a specific company
     */
    public List<InterviewExperience> getInterviewExperiencesByCompany(Integer companyId) {
        return interviewExperienceRepository.findByCompanyIdOrderByExperienceDateDesc(companyId);
    }

    /**
     * Get Interview Experiences for a specific company by name
     */
    public List<InterviewExperience> getInterviewExperiencesByCompanyName(String companyName) {
        return getAllInterviewExperiences().stream()
                .filter(e -> e.getCompany().getName().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    /**
     * Get preparation data grouped by company
     * Returns a map with company name as key and object containing OA questions and interview experiences
     */
    public Map<String, Map<String, Object>> getPreparationHubData() {
        List<OAQuestion> allQuestions = getAllOAQuestions();
        List<InterviewExperience> allExperiences = getAllInterviewExperiences();

        // Create a combined map grouped by company
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();

        // Group by company
        var questionsByCompany = allQuestions.stream()
                .collect(Collectors.groupingBy(q -> q.getCompany().getName()));

        var experiencesByCompany = allExperiences.stream()
                .collect(Collectors.groupingBy(e -> e.getCompany().getName()));

        // Get all unique company names
        var allCompanyNames = new LinkedHashMap<String, Boolean>();
        questionsByCompany.keySet().forEach(name -> allCompanyNames.putIfAbsent(name, true));
        experiencesByCompany.keySet().forEach(name -> allCompanyNames.putIfAbsent(name, true));

        // Build the result map
        allCompanyNames.forEach((companyName, unused) -> {
            Map<String, Object> companyData = new LinkedHashMap<>();
            companyData.put("name", companyName);
            companyData.put("questions", questionsByCompany.getOrDefault(companyName, List.of()));
            companyData.put("experiences", experiencesByCompany.getOrDefault(companyName, List.of()));
            result.put(companyName, companyData);
        });

        return result;
    }
}
