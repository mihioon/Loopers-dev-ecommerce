package com.loopers.domain.brand;

public class BrandImageCommand {
    public record Create(
            Long brandId,
            String imageUrl,
            ImageType imageType
    ) {
    }
}
