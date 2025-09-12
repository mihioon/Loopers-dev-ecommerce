package com.loopers.domain.ranking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface RankingRepository {
    
    Page<ProductRankingInfo> getTopProducts(LocalDate date, Pageable pageable);
    
    Optional<ProductRankingInfo> getProductRanking(Long productId, LocalDate date);
}