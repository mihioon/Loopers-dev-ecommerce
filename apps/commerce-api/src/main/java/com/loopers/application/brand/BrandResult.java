package com.loopers.application.brand;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandImageInfo;

import java.util.List;

public record BrandResult(
        Long id,
        String name,
        String description,
        List<BrandImageInfo> images
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
