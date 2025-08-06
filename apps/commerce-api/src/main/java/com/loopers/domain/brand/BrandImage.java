package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "brand_image")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Brand.ImageType imageType;

    public BrandImage(
            final String imageUrl,
            final Brand.ImageType imageType
    ) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            //throw new CoreException(ErrorType.BAD_REQUEST, "이미지 URL은 필수입니다.");
        }
        if (imageType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미지 타입은 필수입니다.");
        }

        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Brand.ImageType getImageType() {
        return imageType;
    }
}
