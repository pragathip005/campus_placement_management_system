package com.crms.placement.service.ranking;

import com.crms.placement.dto.RankListEntry;
import java.util.List;

/**
 * Strategy Pattern — defines the contract for all ranking algorithms.
 * Each strategy ranks students differently based on company requirements.
 */
public interface RankingStrategy {
    List<RankListEntry> rank(List<RankListEntry> entries);
    String getStrategyName();
}