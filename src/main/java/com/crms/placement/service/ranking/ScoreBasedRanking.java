package com.crms.placement.service.ranking;

import com.crms.placement.dto.RankListEntry;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Ranks students purely by OA score descending.
 * Suitable for companies like Google where coding performance is primary.
 */
@Component
public class ScoreBasedRanking implements RankingStrategy {

    @Override
    public List<RankListEntry> rank(List<RankListEntry> entries) {
        return entries.stream()
                .sorted(Comparator.comparingInt(RankListEntry::getOaScore).reversed())
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "Score Based";
    }
}