package com.loopers.application.product;

import com.loopers.domain.product.dto.ProductCommand;
import com.loopers.domain.product.dto.ProductQuery;

import java.math.BigDecimal;
import java.util.List;

public class ProductCriteria {
    public record Summary(
            String category,
            Long brandId,
            String sortType,
            int page,
            int size,
            String loginId
    ) {
        public ProductQuery.Summary toCommand() {
            return new ProductQuery.Summary(
                    category,
                    brandId,
                    ProductQuery.Summary.SortType.from(sortType),
                    page,
                    size
            );
        }
    }

    public record Detail(
            Long productId,
            Long userId
    ) {}

    public record Create(
            String name,
            String description,
            BigDecimal price,
            String category,
            Long brandId,
            Integer initialStock,
            List<ProductCommand.ImageDto> images,
            ProductCommand.DetailDto detail
    ) {}
}
