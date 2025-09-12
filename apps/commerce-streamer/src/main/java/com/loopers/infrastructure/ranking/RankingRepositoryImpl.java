package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {
    
    private final RedisTemplate<String, String> rankingRedisTemplate;
    
    private static final String RANKING_KEY_PREFIX = "ranking:all:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final long TTL_HOURS = 48;

    @Override
    public void updateProductScores(Map<Long, Double> productScores, LocalDate date) {
        if (productScores.isEmpty()) {
            return;
        }
        
        String rankingKey = buildRankingKey(date);
        
        try {
            // Redis Pipeline
            rankingRedisTemplate.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                for (Map.Entry<Long, Double> entry : productScores.entrySet()) {
                    String productIdStr = entry.getKey().toString();
                    double score = entry.getValue();
                    rankingRedisTemplate.opsForZSet().incrementScore(rankingKey, productIdStr, score);
                }
                rankingRedisTemplate.expire(rankingKey, TTL_HOURS, TimeUnit.HOURS);
                return null;
            });
            
            log.debug("Updated {} product scores for date: {}", productScores.size(), date);
        } catch (Exception e) {
            log.error("Failed to update product scores batch: date={}, count={}", 
                     date, productScores.size(), e);
            throw new RuntimeException("Failed to update ranking scores batch", e);
        }
    }

    private String buildRankingKey(LocalDate date) {
        return RANKING_KEY_PREFIX + date.format(DATE_FORMATTER);
    }
}
