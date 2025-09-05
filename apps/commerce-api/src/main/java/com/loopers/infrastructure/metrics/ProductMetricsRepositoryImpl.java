package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.domain.metrics.ProductMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {
    
    private final ProductMetricsJpaRepository productMetricsJpaRepository;
    
    @Override
    public ProductMetrics save(ProductMetrics metrics) {
        return productMetricsJpaRepository.save(metrics);
    }
    
    @Override
    public Optional<ProductMetrics> findByProductIdAndMetricDate(Long productId, LocalDate metricDate) {
        return productMetricsJpaRepository.findByProductIdAndMetricDate(productId, metricDate);
    }
    
    @Override
    @Transactional
    public void incrementLikeCount(Long productId, LocalDate metricDate, int delta) {
        int updatedRows = productMetricsJpaRepository.incrementLikeCount(productId, metricDate, delta);
        if (updatedRows == 0) {
            ProductMetrics metrics = new ProductMetrics(productId, metricDate);
            metrics.incrementLikeCount(delta);
            productMetricsJpaRepository.save(metrics);
        }
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long productId, LocalDate metricDate, int delta) {
        int updatedRows = productMetricsJpaRepository.incrementViewCount(productId, metricDate, delta);
        if (updatedRows == 0) {
            ProductMetrics metrics = new ProductMetrics(productId, metricDate);
            metrics.incrementViewCount(delta);
            productMetricsJpaRepository.save(metrics);
        }
    }
}
