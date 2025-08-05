package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductResult;

import java.math.BigDecimal;
import java.util.List;

public record GetProducts() {
    public record V1() {
        public record Request(
                String category,
                Long brandId,
                String sortType,
                int page,
                int size
        ) {
            //toCriteria
            public ProductCriteria.Summery toCriteria(String loginId) {
                return new ProductCriteria.Summery(
                        category,
                        brandId,
                        sortType,
                        page,
                        size,
                        loginId
                );
            }
        }

        public record Response(
                List<Item> products,
                int currentPage,
                int totalPages,
                long totalElements,
                boolean hasNext
        ) {
            public static Response from(ProductResult.Summery result) {
                List<Response.Item> responseItems = result.products().stream()
                        .map(Response.Item::from)
                        .toList();
                        
                return new Response(
                        responseItems,
                        result.currentPage(),
                        result.totalPages(),
                        result.totalElements(),
                        result.hasNext()
                );
            }

            public record Item(
                    Long id,
                    String name,
                    String description,
                    BigDecimal price,
                    String category,
                    Long brandId,
                    Long likeCount,
                    Boolean isLikedByUser
            ){
                public static Item from(ProductResult.Summery.Item summeryItem) {
                    return new Item(
                            summeryItem.id(),
                            summeryItem.name(),
                            summeryItem.description(),
                            summeryItem.price(),
                            summeryItem.category(),
                            summeryItem.brandId(),
                            summeryItem.likeCount(),
                            summeryItem.isLikedByUser()
                    );
                }
            }
        }
    }
}
