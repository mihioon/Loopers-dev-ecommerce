package com.loopers.domain.catalog.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "product")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column
    private String category;
    
    @Column(nullable = false)
    private Long brandId;

    public Product(String name, String description, BigDecimal price, String category, Long brandId) {
        if (name == null || name.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품명은 필수입니다.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다.");
        }
        if (brandId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 ID는 필수입니다.");
        }

        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brandId = brandId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public Long getBrandId() { return brandId; }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private List<ProductImage> images = new ArrayList<>();
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private ProductDetail detail;

    public List<ProductImage> getImages() { return images; }
    public ProductDetail getDetail() { return detail; }
    public void setDetail(ProductDetail detail) { this.detail = detail; }
    public void addImage(ProductImage image) { this.images.add(image); }

    public enum ImageType {
        MAIN, THUMBNAIL, DETAIL, EXTRA
    }

    @Table(name = "product_image")
    @Entity
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProductImage extends BaseEntity {
        @Column(nullable = false)
        private String imageUrl;
        
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ImageType imageType;

        public ProductImage(Long productId, String imageUrl, ImageType imageType) {
            this.imageUrl = imageUrl;
            this.imageType = imageType;
        }

        public String getImageUrl() { return imageUrl; }
        public ImageType getImageType() { return imageType; }
    }

    @Table(name = "product_detail")
    @Entity
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProductDetail extends BaseEntity {
        @Column(columnDefinition = "TEXT")
        private String description;

        public ProductDetail(Long productId, String description) {
            this.description = description;
        }

        public String getDetailContent() { return description; }
    }
}
