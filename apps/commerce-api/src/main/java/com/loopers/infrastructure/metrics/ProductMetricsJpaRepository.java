package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.domain.metrics.ProductMetricsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, ProductMetricsId> {
    
    Optional<ProductMetrics> findByProductIdAndMetricDate(Long productId, LocalDate metricDate);
    
    @Modifying
    @Query("UPDATE ProductMetrics m SET m.likeCount = m.likeCount + :delta, m.likeChange = m.likeChange + :delta, m.lastUpdated = CURRENT_TIMESTAMP WHERE m.productId = :productId AND m.metricDate = :metricDate")
    int incrementLikeCount(@Param("productId") Long productId, @Param("metricDate") LocalDate metricDate, @Param("delta") int delta);
    
    @Modifying
    @Query("UPDATE ProductMetrics m SET m.viewCount = m.viewCount + :delta, m.lastUpdated = CURRENT_TIMESTAMP WHERE m.productId = :productId AND m.metricDate = :metricDate")
    int incrementViewCount(@Param("productId") Long productId, @Param("metricDate") LocalDate metricDate, @Param("delta") int delta);
}
