package com.loopers.domain.metrics;


import java.time.LocalDate;
import java.util.Optional;

public interface ProductMetricsRepository {
    
    ProductMetrics save(ProductMetrics metrics);
    
    Optional<ProductMetrics> findByProductIdAndMetricDate(Long productId, LocalDate metricDate);

    void incrementLikeCount(Long productId, LocalDate metricDate, int delta);
    
    void incrementViewCount(Long productId, LocalDate metricDate, int delta);
}
