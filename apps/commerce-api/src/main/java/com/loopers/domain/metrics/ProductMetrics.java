package com.loopers.domain.metrics;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "product_metrics", indexes = {
    @Index(name = "idx_product_date", columnList = "product_id, metric_date"),
    @Index(name = "idx_metric_date", columnList = "metric_date")
})
@IdClass(ProductMetricsId.class)
public class ProductMetrics {
    
    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Id
    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;
    
    @Column(name = "like_change", nullable = false)
    private Integer likeChange; // 일별 변화량
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;
    
    @Column(name = "sales_count", nullable = false)
    private Integer salesCount;
    
    @Column(name = "sales_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal salesAmount;
    
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;
    
    protected ProductMetrics() {
        // JPA
    }
    
    public ProductMetrics(Long productId, LocalDate metricDate) {
        this.productId = productId;
        this.metricDate = metricDate;
        this.likeCount = 0;
        this.likeChange = 0;
        this.viewCount = 0;
        this.salesCount = 0;
        this.salesAmount = BigDecimal.ZERO;
        this.lastUpdated = Instant.now();
    }
    
    public void incrementLikeCount(int delta) {
        this.likeCount += delta;
        this.likeChange += delta;
        this.lastUpdated = Instant.now();
    }
    
    public void incrementViewCount(int delta) {
        this.viewCount += delta;
        this.lastUpdated = Instant.now();
    }
    
    public void incrementSalesCount(int quantity, BigDecimal amount) {
        this.salesCount += quantity;
        this.salesAmount = this.salesAmount.add(amount);
        this.lastUpdated = Instant.now();
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public LocalDate getMetricDate() {
        return metricDate;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public Integer getLikeChange() {
        return likeChange;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public Integer getSalesCount() {
        return salesCount;
    }
    
    public BigDecimal getSalesAmount() {
        return salesAmount;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
}