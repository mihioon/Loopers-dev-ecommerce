package com.loopers.domain.product.dto;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductInfo {
    public record Summary(
            List<Summary.Item> products,
            int currentPage,
            int totalPages,
            long totalElements,
            boolean hasNext
    ) {
        public static Summary from(List<Product> products, int currentPage, int totalPages, long totalElements, boolean hasNext) {
            return new Summary(
                    products.stream()
                            .map(Summary.Item::from)
                            .toList(),
                    currentPage,
                    totalPages,
                    totalElements,
                    hasNext
            );
        }

        public record Item(
                Long id,
                String name,
                String description,
                BigDecimal price,
                String category,
                Long brandId
        ) {
            public static Item from(Product product) {
                return new Item(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory(),
                        product.getBrandId()
                );
            }
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
            BrandInfo brandInfo,
            List<ImageInfo> images,
            DetailInfo detail
    ) {
        public static Detail from(Product product, List<ImageInfo> images, DetailInfo detail, BrandInfo brandInfo) {
            return new Detail(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategory(),
                    product.getBrandId(),
                    brandInfo,
                    images,
                    detail
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
