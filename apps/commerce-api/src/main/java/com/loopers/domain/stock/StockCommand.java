package com.loopers.domain.stock;

public class StockCommand {
    
    public record Initialize(
            Long productId,
            Integer initialQuantity
    ) {}
    
    public record Reduce(
            Long productId,
            Integer amount
    ) {}
    
    public record ReduceAvailable(
            Long productId,
            Integer amount
    ) {}
    
    public record Reserve(
            Long productId,
            Integer amount
    ) {}
    
    public record ReleaseReserved(
            Long productId,
            Integer amount
    ) {}
}
