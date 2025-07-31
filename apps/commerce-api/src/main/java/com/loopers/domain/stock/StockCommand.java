package com.loopers.domain.stock;

public class StockCommand {
    
    public record Initialize(
            Long productId,
            Integer initialQuantity
    ) {}
    
    public record Adjustment(
            Long productId,
            Integer amount,
            StockOperationType operation
    ) {}
    
    public enum StockOperationType {
        ADD, REDUCE, RESERVE, RELEASE_RESERVED
    }
}
