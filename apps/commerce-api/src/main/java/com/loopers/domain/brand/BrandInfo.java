package com.loopers.domain.brand;

import java.util.List;

public record BrandInfo(
        Long id,
        String name,
        String description,
        List<BrandImageInfo> images
) {
    public static BrandInfo from(final Brand brand, final List<BrandImageInfo> images) {
        return new BrandInfo(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                images
        );
    }
}
