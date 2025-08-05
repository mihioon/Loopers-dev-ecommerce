package com.loopers.application.product;

import com.loopers.domain.product.ProductCommand;

import java.math.BigDecimal;
import java.util.List;

public class ProductCriteria {
    public record Summery(
            String category,
            Long brandId,
            String sortType,
            int page,
            int size,
            String loginId
    ) {
        public ProductCommand.Summery toCommand() {
            return new ProductCommand.Summery(
                    category,
                    brandId,
                    ProductCommand.Summery.SortType.from(sortType),
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
            List<ProductCommand.ImageCommand> images,
            ProductCommand.DetailCommand detail
    ) {}
}
