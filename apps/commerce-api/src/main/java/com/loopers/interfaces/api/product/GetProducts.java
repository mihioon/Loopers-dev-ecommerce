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
            public ProductCriteria.Summary toCriteria(String loginId) {
                return new ProductCriteria.Summary(
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
            public static Response from(ProductResult.Summary result) {
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
                public static Item from(ProductResult.Summary.Item summaryItem) {
                    return new Item(
                            summaryItem.id(),
                            summaryItem.name(),
                            summaryItem.description(),
                            summaryItem.price(),
                            summaryItem.category(),
                            summaryItem.brandId(),
                            summaryItem.likeCount(),
                            summaryItem.isLikedByUser()
                    );
                }
            }
        }
    }
}
