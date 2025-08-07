package com.loopers.domain.product.dto;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductInfo {
    public record Summary(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String category,
            Long brandId
    ) {
        public static Summary from(Product product) {
            return new Summary(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategory(),
                    product.getBrandId()
            );
        }
    }

    public record Basic(
            Long id,
            String name,
            BigDecimal price
    ) {
        public static Basic from(Product product) {
            return new Basic(
                    product.getId(),
                    product.getName(),
                    product.getPrice()
            );
        }
    }

    public record Detail(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String category,
            Long brandId,
            List<ImageInfo> images,
            DetailInfo detail
    ) {
        public static Detail from(Product product) {
            return new Detail(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategory(),
                    product.getBrandId(),
                    product.getImages().stream()
                            .map(ImageInfo::from)
                            .toList(),
                    product.getDetail() != null ? DetailInfo.from(product.getDetail()) : null
            );
        }
    }

    public record ImageInfo(
            Long id,
            Long productId,
            String imageUrl,
            Product.ImageType imageType
    ) {
        public static ImageInfo from(Product.ProductImage productImage) {
            return new ImageInfo(
                    productImage.getId(),
                    null,
                    productImage.getImageUrl(),
                    productImage.getImageType()
            );
        }
    }

    public record DetailInfo(
            Long id,
            Long productId,
            String detailContent
    ) {
        public static DetailInfo from(Product.ProductDetail productDetail) {
            return new DetailInfo(
                    productDetail.getId(),
                    null,
                    productDetail.getDetailContent()
            );
        }
    }
}
