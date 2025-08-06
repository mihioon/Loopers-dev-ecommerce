package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "brand")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "brand_id")
    private List<BrandImage> images = new ArrayList<>();

    public Brand(
            final String name,
            final String description
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드명은 필수입니다.");
        }

        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<BrandImage> getImages() {
        return new ArrayList<>(images);
    }

    public void addImage(BrandImage image) {
        if (image != null) {
            this.images.add(image);
            image.setBrand(this);
        }
    }

    public void removeImage(BrandImage image) {
        this.images.remove(image);
        if (image != null) {
            image.setBrand(null);
        }
    }

    public void clearImages() {
        this.images.clear();
    }

    public enum ImageType {
        LOGO,
        BANNER, 
        THUMBNAIL;

        public static ImageType from(String value) {
            if (value == null || value.trim().isEmpty()) {
                throw new CoreException(ErrorType.BAD_REQUEST, "이미지 타입은 필수입니다.");
            }

            for (ImageType imageType : ImageType.values()) {
                if (imageType.name().equalsIgnoreCase(value.trim())) {
                    return imageType;
                }
            }

            throw new CoreException(ErrorType.BAD_REQUEST, 
                "지원하지 않는 이미지 타입입니다: " + value + ". 지원 타입: LOGO, BANNER, THUMBNAIL");
        }
    }
}
