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

    @Column(nullable = false)
    private Long brandId;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;


    public BrandImage(
            final Long brandId,
            final String imageUrl,
            final ImageType imageType
    ) {
        if (brandId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 ID는 필수입니다.");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            //throw new CoreException(ErrorType.BAD_REQUEST, "이미지 URL은 필수입니다.");
        }
        if (imageType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미지 타입은 필수입니다.");
        }

        this.brandId = brandId;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

    public Long getBrandId() {
        return brandId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ImageType getImageType() {
        return imageType;
    }

}
