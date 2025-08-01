package com.loopers.domain.catalog.brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Brand save(Brand brand);

    Boolean existsByName(String name);

    Optional<Brand> findById(Long brandId);

    // BrandImage 관련 메서드들
    Brand.BrandImage save(Brand.BrandImage brandImage);

    List<Brand.BrandImage> saveAll(List<Brand.BrandImage> brandImages);

    Optional<Brand.BrandImage> findBrandImageById(Long brandImageId);

    List<Brand.BrandImage> findBrandImagesByBrandId(Long brandId);
}
