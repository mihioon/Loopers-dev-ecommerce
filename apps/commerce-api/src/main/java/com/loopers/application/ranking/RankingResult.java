package com.loopers.application.ranking;

import com.loopers.domain.ranking.ProductRankingResult;

import java.time.LocalDate;
import java.util.List;

public record RankingResult() {
    public record Query(
            List<ProductRankingResult> rankings,
            int currentPage,
            int totalPages,
            long totalElements,
            boolean hasNext,
            LocalDate targetDate
    ) {}
}
