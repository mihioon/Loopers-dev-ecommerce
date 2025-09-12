package com.loopers.domain.ranking;

public record ProductRankingInfo(
        Long productId,
        double score,
        int rank
) {
    public static ProductRankingInfo of(Long productId, double score, int rank) {
        return new ProductRankingInfo(productId, score, rank);
    }
}
