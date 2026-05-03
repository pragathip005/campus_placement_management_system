package com.crms.placement.service.ranking;

import com.crms.placement.dto.RankListEntry;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Ranks students by composite score: (OA score × 0.7) + (CGPA × 3).
 * Balanced approach used by most companies like Microsoft.
 */
@Component
public class WeightedRanking implements RankingStrategy {

    @Override
    public List<RankListEntry> rank(List<RankListEntry> entries) {
        entries.forEach(e -> {
            double composite = (e.getOaScore() * 0.7) + (e.getCgpa() * 3);
            e.setCompositeScore(Math.round(composite * 100.0) / 100.0);
        });

        return entries.stream()
                .sorted(Comparator.comparingDouble(RankListEntry::getCompositeScore).reversed())
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "Weighted (Score + CGPA)";
    }
}