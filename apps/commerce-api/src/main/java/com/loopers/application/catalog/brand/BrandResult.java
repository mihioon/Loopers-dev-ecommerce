package com.loopers.application.catalog.brand;

import com.loopers.domain.catalog.brand.BrandInfo;

import java.util.List;

public record BrandResult(
        Long id,
        String name,
        String description,
        List<BrandInfo.BrandImageInfo> images
) {
    public static BrandResult from(BrandInfo brandInfo) {
        return new BrandResult(
                brandInfo.id(),
                brandInfo.name(),
                brandInfo.description(),
                brandInfo.images()
        );
    }
}
