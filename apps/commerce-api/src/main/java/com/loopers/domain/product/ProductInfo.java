package com.loopers.domain.product;

import java.math.BigDecimal;
import java.util.List;

public class ProductInfo {
    public record Summery(
            List<Summery.Item> products,
            int currentPage,
            int totalPages,
            long totalElements,
            boolean hasNext
    ) {
        public static Summery from(List<Product> products, int currentPage, int totalPages, long totalElements, boolean hasNext) {
            return new Summery(
                    products.stream()
                            .map(Summery.Item::from)
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
        public static Detail from(Product product, List<ImageInfo> images, DetailInfo detail) {
            return new Detail(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategory(),
                    product.getBrandId(),
                    images,
                    detail
            );
        }

    }

    public record BrandInfo(
            Long id,
            String name,
            String description
    ) {}

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
