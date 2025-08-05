package com.loopers.domain.product;

public record StockInfo(
        Long id,
        Long productId,
        Integer quantity
) {
    public static StockInfo from(ProductStock productStock) {
        return new StockInfo(
                productStock.getId(),
                productStock.getProductId(),
                productStock.getQuantity()
        );
    }
}
