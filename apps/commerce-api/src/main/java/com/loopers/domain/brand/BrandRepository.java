package com.loopers.domain.brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Brand save(Brand brand);

    Boolean existsByName(String name);

    Optional<Brand> findById(Long brandId);

    // BrandImage 관련 메서드들
    BrandImage save(BrandImage brandImage);

    List<BrandImage> saveAll(List<BrandImage> brandImages);

    Optional<BrandImage> findBrandImageById(Long brandImageId);

    List<BrandImage> findBrandImagesByBrandId(Long brandId);

    default Optional<Brand> findWithImagesById(Long brandId) {
        return null;
    }
}
