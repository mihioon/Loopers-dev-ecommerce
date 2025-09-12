package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductRankingInfo;
import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String RANKING_KEY_PREFIX = "ranking:all:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    @Override
    public Page<ProductRankingInfo> getTopProducts(LocalDate date, Pageable pageable) {
        String rankingKey = buildRankingKey(date);
        
        try {
            long start = pageable.getOffset();
            long end = start + pageable.getPageSize() - 1;
            
            Set<TypedTuple<String>> rankingsWithScore = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(rankingKey, start, end);
            
            Long totalCount = redisTemplate.opsForZSet().zCard(rankingKey);
            
            List<ProductRankingInfo> rankings = new ArrayList<>();
            int rank = (int) start + 1;
            
            if (rankingsWithScore != null) {
                for (TypedTuple<String> tuple : rankingsWithScore) {
                    try {
                        Long productId = Long.valueOf(tuple.getValue());
                        Double score = tuple.getScore() != null ? tuple.getScore() : 0.0;
                        rankings.add(ProductRankingInfo.of(productId, score, rank++));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid product ID in ranking: {}", tuple.getValue());
                    }
                }
            }
            
            return new PageImpl<>(rankings, pageable, totalCount != null ? totalCount : 0);
            
        } catch (Exception e) {
            log.error("Failed to get top products: date={}, page={}", date, pageable, e);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }
    
    @Override
    public Optional<ProductRankingInfo> getProductRanking(Long productId, LocalDate date) {
        String rankingKey = buildRankingKey(date);
        String productIdStr = productId.toString();
        
        try {
            Double score = redisTemplate.opsForZSet().score(rankingKey, productIdStr);
            if (score == null) {
                return Optional.empty();
            }
            
            Long rank = redisTemplate.opsForZSet().reverseRank(rankingKey, productIdStr);
            int rankInt = rank != null ? rank.intValue() + 1 : -1;
            
            return Optional.of(ProductRankingInfo.of(productId, score, rankInt));
            
        } catch (Exception e) {
            log.error("Failed to get product ranking: productId={}, date={}", productId, date, e);
            return Optional.empty();
        }
    }
    
    private String buildRankingKey(LocalDate date) {
        return RANKING_KEY_PREFIX + date.format(DATE_FORMATTER);
    }
}
