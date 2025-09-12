package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingQueryService {
    
    private final RankingRepository rankingRepository;

    public Page<ProductRankingInfo> getTopProducts(LocalDate date, Pageable pageable) {
        return rankingRepository.getTopProducts(date, pageable);
    }

    public Optional<ProductRankingInfo> getProductRanking(Long productId, LocalDate date) {
        return rankingRepository.getProductRanking(productId, date);
    }
}
