package com.loopers.domain.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class ProductMetricsService {
    
    private final ProductMetricsRepository productMetricsRepository;
    
    @Transactional
    public void incrementLikeCount(Long productId, LocalDate date, int delta) {
        ProductMetrics metrics = getOrCreateMetrics(productId, date);
        metrics.incrementLikeCount(delta);
        productMetricsRepository.save(metrics);
    }
    
    @Transactional
    public void incrementViewCount(Long productId, LocalDate date, int delta) {
        ProductMetrics metrics = getOrCreateMetrics(productId, date);
        metrics.incrementViewCount(delta);
        productMetricsRepository.save(metrics);
    }
    
    @Transactional
    public void incrementSalesCount(Long productId, LocalDate date, int quantity, BigDecimal amount) {
        ProductMetrics metrics = getOrCreateMetrics(productId, date);
        metrics.incrementSalesCount(quantity, amount);
        productMetricsRepository.save(metrics);
    }
    
    private ProductMetrics getOrCreateMetrics(Long productId, LocalDate date) {
        return productMetricsRepository
            .findByProductIdAndMetricDate(productId, date)
            .orElseGet(() -> new ProductMetrics(productId, date));
    }
}