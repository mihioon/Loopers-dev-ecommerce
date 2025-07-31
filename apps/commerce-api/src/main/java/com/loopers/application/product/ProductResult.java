package com.loopers.application.product;

import com.loopers.domain.like.LikeCountInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.stock.StockInfo;

import java.math.BigDecimal;
import java.util.List;

public class ProductResult{
    public record Summery(
            List<Summery.Item> products,
            int currentPage,
            int totalPages,
            long totalElements,
            boolean hasNext
    ) {
        public static Summery from(
                ProductInfo.Summery productInfo,
                LikeCountInfo likeCount,
                LikeCountInfo isLikedByUser
        ) {
            return new Summery(
                    productInfo.products().stream()
                            .map(item -> Item.from(item, 
                                    likeCount != null ? likeCount.likeCounts().get(item.id()) : null,
                                    isLikedByUser != null && isLikedByUser.likeCounts().get(item.id()) == 1L
                            ))
                            .toList(),
                    productInfo.currentPage(),
                    productInfo.totalPages(),
                    productInfo.totalElements(),
                    productInfo.hasNext()
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
        ) {
            public static Summery.Item from(ProductInfo.Summery.Item productInfo, Long likeCount, Boolean isLikedByUser) {
                return new Summery.Item(
                        productInfo.id(),
                        productInfo.name(),
                        productInfo.description(),
                        productInfo.price(),
                        productInfo.category(),
                        productInfo.brandId(),
                        likeCount,
                        isLikedByUser
                );
            }
        }
    }

    public record Detail (
            Long id,
            String name,
            String description,
            BigDecimal price,
            String category,
            Long brandId,
            Integer availableQuantity,
            List<ImageInfo> images,
            Long likeCount,
            Boolean isLikedByUser
    ) {
        public static ProductResult.Detail from(
                ProductInfo.Detail productInfo,
                StockInfo stockInfo,
                Long likeCount,
                Boolean isLikedByUser
        ) {
            return new ProductResult.Detail(
                    productInfo.id(),
                    productInfo.name(),
                    productInfo.description(),
                    productInfo.price(),
                    productInfo.category(),
                    productInfo.brandId(),
                    stockInfo.availableQuantity(),
                    productInfo.images().stream()
                            .map(ProductResult.ImageInfo::from)
                            .toList(),
                    likeCount,
                    isLikedByUser
            );
        }
    }

    public record ImageInfo(
            Long id,
            Long productId,
            String imageUrl,
            ImageType imageType
    ) {
        public enum ImageType {
            MAIN,
            THUMBNAIL,
            DETAIL,
            EXTRA
        }

        public static ProductResult.ImageInfo from(ProductInfo.ImageInfo imageInfo) {
            return new ProductResult.ImageInfo(
                    imageInfo.id(),
                    imageInfo.productId(),
                    imageInfo.imageUrl(),
                    ImageType.valueOf(imageInfo.imageType().name())
            );
        }
    }
}
