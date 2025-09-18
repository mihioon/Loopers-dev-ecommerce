package com.loopers.application.ranking;

import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.ranking.ProductRankingInfo;
import com.loopers.domain.ranking.ProductRankingResult;
import com.loopers.domain.ranking.RankingQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class RankingFacade {
    
    private final RankingQueryService rankingQueryService;
    private final ProductService productService;
    
    public RankingResult.Query getRankings(RankingCriteria.Query criteria) {
        Pageable pageable = PageRequest.of(criteria.page() - 1, criteria.size());
        Page<ProductRankingInfo> rankingPage = rankingQueryService.getTopProducts(criteria.targetDate(), pageable);
        
        List<ProductRankingResult> enrichedRankings = enrichWithProductInfo(rankingPage.getContent());
        
        return new RankingResult.Query(
                enrichedRankings,
                criteria.page(),
                rankingPage.getTotalPages(),
                rankingPage.getTotalElements(),
                rankingPage.hasNext(),
                criteria.targetDate()
        );
    }
    
    private List<ProductRankingResult> enrichWithProductInfo(List<ProductRankingInfo> rankings) {
        if (rankings.isEmpty()) {
            return List.of();
        }
        
        Set<Long> productIds = rankings.stream()
                .map(ProductRankingInfo::productId)
                .collect(Collectors.toSet());
        
        Map<Long, ProductInfo.Summary> productInfoMap = productService.getProductSummaries(productIds);
        
        return rankings.stream()
                .map(ranking -> {
                    ProductInfo.Summary productInfo = productInfoMap.get(ranking.productId());
                    if (productInfo != null) {
                        return ProductRankingResult.of(productInfo, ranking);
                    } else {
                        log.warn("Product info not found for ranking: productId={}", ranking.productId());
                        return null;
                    }
                })
                .filter(result -> result != null)
                .toList();
    }
}
