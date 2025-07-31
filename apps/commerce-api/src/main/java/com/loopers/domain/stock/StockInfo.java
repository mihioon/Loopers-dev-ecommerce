package com.loopers.domain.stock;

public record StockInfo(
        Long id,
        Long productId,
        Integer quantity,
        Integer reservedQuantity,
        Integer availableQuantity
) {
    public static StockInfo from(ProductStock productStock) {
        return new StockInfo(
                productStock.getId(),
                productStock.getProductId(),
                productStock.getQuantity(),
                productStock.getReservedQuantity(),
                productStock.getAvailableQuantity()
        );
    }
}
