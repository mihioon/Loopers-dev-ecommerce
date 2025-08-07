package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandResult;
import com.loopers.domain.brand.BrandInfo;

import java.util.List;

public record GetBrand() {
    public record V1() {
        public record Response(
                Long id,
                String name,
                String description,
                List<BrandInfo.BrandImageInfo> images
        ) {
            public static Response from(BrandResult brandResult) {
                return new Response(
                        brandResult.id(),
                        brandResult.name(),
                        brandResult.description(),
                        brandResult.images()
                );
            }
        }
    }
}
