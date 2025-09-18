package com.loopers.domain.ranking;

import com.loopers.domain.product.dto.ProductInfo;

public record ProductRankingResult(
        ProductInfo.Summary productInfo,
        ProductRankingInfo rankingInfo,
        int rank
) {
    public static ProductRankingResult of(ProductInfo.Summary productInfo, ProductRankingInfo rankingInfo) {
        return new ProductRankingResult(productInfo, rankingInfo, rankingInfo.rank());
    }
}
