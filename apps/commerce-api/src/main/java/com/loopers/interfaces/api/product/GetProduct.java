package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;
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
                        productResult.availableQuantity(),
                        productResult.brandId(),
                        productResult.category(),
                        productResult.images() != null && !productResult.images().isEmpty() 
                            ? productResult.images().get(0).imageUrl() 
                            : null,
                        productResult.likeCount(),
                        productResult.isLikedByUser()
                );
            }
        }
    }
}
