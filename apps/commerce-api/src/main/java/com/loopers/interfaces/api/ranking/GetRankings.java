package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingCriteria;
import com.loopers.application.ranking.RankingResult;
import com.loopers.domain.ranking.ProductRankingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record GetRankings() {
    public record V1() {
        public record Request(
                String date,
                int page,
                int size
        ) {
            public Request {
                if (page <= 0) page = 1;
                if (size <= 0) size = 20;
                if (size > 100) size = 100;
            }
            
            public RankingCriteria.Query toCriteria() {
                LocalDate targetDate = (date != null && !date.isBlank()) 
                    ? LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    : LocalDate.now();
                return new RankingCriteria.Query(targetDate, page, size);
            }
        }

        public record Response(
                List<RankingItem> rankings,
                int currentPage,
                int totalPages,
                long totalElements,
                boolean hasNext,
                String targetDate
        ) {
            public static Response from(RankingResult.Query result) {
                List<RankingItem> rankingItems = result.rankings().stream()
                        .map(RankingItem::from)
                        .toList();
                
                String targetDateStr = result.targetDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                
                return new Response(
                        rankingItems,
                        result.currentPage(),
                        result.totalPages(),
                        result.totalElements(),
                        result.hasNext(),
                        targetDateStr
                );
            }
            
            public record RankingItem(
                    int rank,
                    Long productId,
                    String productName,
                    BigDecimal price,
                    String description,
                    String category,
                    Long brandId,
                    Long likeCount,
                    double score
            ) {
                public static RankingItem from(ProductRankingResult result) {
                    return new RankingItem(
                            result.rank(),
                            result.productInfo().id(),
                            result.productInfo().name(),
                            result.productInfo().price(),
                            result.productInfo().description(),
                            result.productInfo().category(),
                            result.productInfo().brandId(),
                            result.productInfo().likeCount(),
                            result.rankingInfo().score()
                    );
                }
            }
        }
    }
}
