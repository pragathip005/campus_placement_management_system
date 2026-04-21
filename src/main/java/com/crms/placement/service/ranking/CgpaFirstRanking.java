package com.crms.placement.service.ranking;

import com.crms.placement.dto.RankListEntry;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Filters students with OA score >= 70, then ranks by CGPA descending.
 * Used by consulting/finance companies like Deloitte.
 */
@Component
public class CgpaFirstRanking implements RankingStrategy {

    private static final int MINIMUM_OA_SCORE = 70;

    @Override
    public List<RankListEntry> rank(List<RankListEntry> entries) {
        return entries.stream()
                .filter(e -> e.getOaScore() >= MINIMUM_OA_SCORE)
                .sorted(Comparator.comparingDouble(RankListEntry::getCgpa).reversed())
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "CGPA First (min OA score 70)";
    }
}