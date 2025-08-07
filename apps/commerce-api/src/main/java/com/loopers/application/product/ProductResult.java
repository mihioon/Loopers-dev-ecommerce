package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.StockInfo;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public class ProductResult{
    public record Summary(
            List<Summary.Item> products,
            int currentPage,
            int totalPages,
            long totalElements,
            boolean hasNext
    ) {
        public static Summary from(
                Page<ProductInfo.Summary> productInfo,
                LikeInfo likeInfo
        ) {
            List<Item> items = productInfo.getContent().stream()
                    .map(product -> Item.from(
                            product,
                            likeInfo.userLiked().isLiked(product.id())
                    ))
                    .toList();

            return new Summary(
                    items,
                    productInfo.getNumber(),
                    productInfo.getTotalPages(),
                    productInfo.getTotalElements(),
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
            public static Summary.Item from(ProductInfo.Summary productInfo, Boolean isLikedByUser) {
                return new Summary.Item(
                        productInfo.id(),
                        productInfo.name(),
                        productInfo.description(),
                        productInfo.price(),
                        productInfo.category(),
                        productInfo.brandId(),
                        productInfo.likeCount(),
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
            BrandInfo brandInfo,
            Integer quantity,
            List<ImageInfo> images,
            Long likeCount,
            Boolean isLikedByUser
    ) {
        public static ProductResult.Detail from(
                ProductInfo.Detail productInfo,
                BrandInfo brandInfo,
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
                    brandInfo,
                    stockInfo.quantity(),
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
