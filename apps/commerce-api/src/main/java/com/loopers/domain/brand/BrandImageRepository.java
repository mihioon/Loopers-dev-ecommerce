package com.loopers.domain.brand;

import java.util.List;
import java.util.Optional;

public interface BrandImageRepository {
    BrandImage save(BrandImage brandImage);

    List<BrandImage> saveAll(List<BrandImage> brandImages);

    Optional<BrandImage> findById(Long brandImageId);

    List<BrandImage> findByBrandId(Long brandId);
}
