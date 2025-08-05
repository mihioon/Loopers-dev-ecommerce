package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;
import com.loopers.domain.brand.BrandInfo;
import java.math.BigDecimal;

public record GetProduct() {
    public record V1() {

        public record Response(
                Long id,
                String name,
                String description,
                BigDecimal price,
                Integer stock,
                Long brandId,
                BrandInfo brandInfo,
                String category,
                String imageUrl,
                Long likeCount,
                Boolean isLikedByUser
        ) {
            public static Response from(ProductResult.Detail productResult) {
                return new Response(
                        productResult.id(),
                        productResult.name(),
                        productResult.description(),
                        productResult.price(),
                        productResult.quantity(),
                        productResult.brandId(),
                        productResult.brandInfo(),
                        productResult.category(),
                        productResult.images() != null && !productResult.images().isEmpty() 
                            ? productResult.images().getFirst().imageUrl()
                            : null,
                        productResult.likeCount(),
                        productResult.isLikedByUser()
                );
            }
        }
    }
}
