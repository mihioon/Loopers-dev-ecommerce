package com.loopers.domain.product.dto;

public class ProductStockCommand {
    
    public record Create(
            Long productId,
            Integer quantity
    ) {}

    public record Reduce(
            Long productId,
            Integer quantity
    ) {}
}
