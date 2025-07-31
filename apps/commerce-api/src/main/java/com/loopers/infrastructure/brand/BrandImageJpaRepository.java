package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.BrandImage;
import com.loopers.domain.brand.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandImageJpaRepository extends JpaRepository<BrandImage, Long> {
    List<BrandImage> findByBrandId(Long brandId);

    List<BrandImage> findByBrandIdAndImageType(Long brandId, ImageType imageType);
}
