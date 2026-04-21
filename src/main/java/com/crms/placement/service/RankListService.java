package com.crms.placement.service;

import com.crms.placement.dto.RankListEntry;
import com.crms.placement.model.Application;
import com.crms.placement.model.ApplicationStatus;
import com.crms.placement.model.OnlineAssessment;
import com.crms.placement.model.Student;
import com.crms.placement.repository.ApplicationRepository;
import com.crms.placement.repository.OnlineAssessmentRepository;
import com.crms.placement.repository.StudentRepository;
import com.crms.placement.service.ranking.CgpaFirstRanking;
import com.crms.placement.service.ranking.RankingStrategy;
import com.crms.placement.service.ranking.ScoreBasedRanking;
import com.crms.placement.service.ranking.WeightedRanking;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RankListService {

    private final ApplicationRepository applicationRepository;
    private final OnlineAssessmentRepository onlineAssessmentRepository;
    private final StudentRepository studentRepository;

    private final ScoreBasedRanking scoreBasedRanking;
    private final WeightedRanking weightedRanking;
    private final CgpaFirstRanking cgpaFirstRanking;

    public RankListService(ApplicationRepository applicationRepository,
                           OnlineAssessmentRepository onlineAssessmentRepository,
                           StudentRepository studentRepository,
                           ScoreBasedRanking scoreBasedRanking,
                           WeightedRanking weightedRanking,
                           CgpaFirstRanking cgpaFirstRanking) {
        this.applicationRepository = applicationRepository;
        this.onlineAssessmentRepository = onlineAssessmentRepository;
        this.studentRepository = studentRepository;
        this.scoreBasedRanking = scoreBasedRanking;
        this.weightedRanking = weightedRanking;
        this.cgpaFirstRanking = cgpaFirstRanking;
    }

    /**
     * Returns the correct strategy based on strategy name string.
     */
    public RankingStrategy resolveStrategy(String strategyName) {
        return switch (strategyName) {
            case "weighted" -> weightedRanking;
            case "cgpa" -> cgpaFirstRanking;
            default -> scoreBasedRanking; // default: score-based
        };
    }

    /**
     * Build rank list for a given opportunity using the chosen strategy.
     */
    public List<RankListEntry> getRankList(Integer opportunityId, String strategyName) {
        List<Application> completedApplications = applicationRepository
                .findByOpportunityIdAndStatus(opportunityId, ApplicationStatus.OA_COMPLETED);

        List<RankListEntry> entries = new ArrayList<>();

        for (Application app : completedApplications) {
            Student student = studentRepository
                    .findById(app.getStudentId().longValue())
                    .orElse(null);
            if (student == null) continue;

            OnlineAssessment oa = onlineAssessmentRepository
                .findByApplication_ApplicationId(app.getApplicationId())
                .orElse(null);

            int score = (oa != null && oa.getScore() != null) ? oa.getScore() : 0;

            entries.add(new RankListEntry(
                    app.getApplicationId(),
                    student.getName(),
                    student.getEmail(),
                    student.getCgpa(),
                    score,
                    student.getBranch(),
                    student.getSrn()
            ));
        }

        // Apply chosen strategy
        RankingStrategy strategy = resolveStrategy(strategyName);
        List<RankListEntry> ranked = strategy.rank(entries);

        // Assign rank numbers
        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setRank(i + 1);
        }

        return ranked;
    }

    /**
     * Write rank list as CSV to the provided PrintWriter.
     */
    public void writeRankListCsv(Integer opportunityId, String strategyName, PrintWriter writer) {
        List<RankListEntry> ranked = getRankList(opportunityId, strategyName);

        writer.println("Rank,Name,SRN,Branch,CGPA,OA Score,Composite Score,Email");

        for (RankListEntry entry : ranked) {
            writer.printf("%d,%s,%s,%s,%.2f,%d,%.2f,%s%n",
                    entry.getRank(),
                    csvSafe(entry.getStudentName()),
                    csvSafe(entry.getSrn()),
                    csvSafe(entry.getBranch()),
                    entry.getCgpa(),
                    entry.getOaScore(),
                    entry.getCompositeScore() != null ? entry.getCompositeScore() : 0.0,
                    csvSafe(entry.getEmail())
            );
        }

        writer.flush();
    }

    private String csvSafe(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}