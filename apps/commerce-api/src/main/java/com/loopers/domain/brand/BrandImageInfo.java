package com.loopers.domain.brand;

public record BrandImageInfo(
        Long id,
        Long brandId,
        String imageUrl,
        ImageType imageType
) {
    public static BrandImageInfo from(final BrandImage brandImage) {
        return new BrandImageInfo(
                brandImage.getId(),
                brandImage.getBrandId(),
                brandImage.getImageUrl(),
                brandImage.getImageType()
        );
    }
}
