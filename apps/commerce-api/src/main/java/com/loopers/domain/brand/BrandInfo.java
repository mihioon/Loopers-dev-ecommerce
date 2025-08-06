package com.loopers.domain.brand;

import java.util.List;

public record BrandInfo(
        Long id,
        String name,
        String description,
        List<BrandInfo.BrandImageInfo> images
) {
    public static BrandInfo from(final Brand brand) {
        return new BrandInfo(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getImages().stream()
                        .map(BrandInfo.BrandImageInfo::from)
                        .toList()
        );
    }

    public record BrandImageInfo(
            Long id,
            Long brandId,
            String imageUrl,
            Brand.ImageType imageType
    ) {
        public static BrandImageInfo from(final BrandImage brandImage) {
            return new BrandImageInfo(
                    brandImage.getId(),
                    brandImage.getBrand() != null ? brandImage.getBrand().getId() : null,
                    brandImage.getImageUrl(),
                    brandImage.getImageType()
            );
        }
    }
}
