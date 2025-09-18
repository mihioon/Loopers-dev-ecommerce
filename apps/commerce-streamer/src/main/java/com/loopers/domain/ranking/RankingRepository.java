package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.Map;

public interface RankingRepository {
    void updateProductScores(Map<Long, Double> productScores, LocalDate date);
}
