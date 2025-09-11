package com.loopers.domain.metrics;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ProductMetricsId implements Serializable {
    
    private Long productId;
    private LocalDate metricDate;
    
    public ProductMetricsId() {} // JPA 용 기본 생성자
    
    public ProductMetricsId(Long productId, LocalDate metricDate) {
        this.productId = productId;
        this.metricDate = metricDate;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public LocalDate getMetricDate() {
        return metricDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMetricsId that)) return false;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(metricDate, that.metricDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, metricDate);
    }
}