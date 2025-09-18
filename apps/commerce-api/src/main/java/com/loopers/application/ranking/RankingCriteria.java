package com.loopers.application.ranking;

import java.time.LocalDate;

public record RankingCriteria() {
    public record Query(
            LocalDate targetDate,
            int page,
            int size
    ) {}
}
