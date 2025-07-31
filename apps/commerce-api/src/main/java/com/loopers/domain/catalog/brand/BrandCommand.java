package com.loopers.domain.catalog.brand;

import java.util.List;

public class BrandCommand {
    public record Create(
            String name,
            String description,
            List<BrandCommand.BrandImageCommand.Create> images
    ) {
    }

    public static class BrandImageCommand {
        public record Create(
                Long brandId,
                String imageUrl,
                Brand.ImageType imageType
        ) {
        }
    }
}