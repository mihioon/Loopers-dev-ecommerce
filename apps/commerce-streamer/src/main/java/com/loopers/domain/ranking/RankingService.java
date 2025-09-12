package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {
    
    private final RankingRepository rankingRepository;
    
    public void updateProductScores(Map<Long, Double> productScores, LocalDate date) {
        rankingRepository.updateProductScores(productScores, date);
    }
}
