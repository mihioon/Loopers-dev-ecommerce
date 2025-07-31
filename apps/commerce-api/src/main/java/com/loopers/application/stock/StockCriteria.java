package com.loopers.application.stock;

public class StockCriteria {
    
    public record Initialize(
            String loginId,
            Long productId,
            Integer initialQuantity
    ) {}
    
    public record Reduce(
            String loginId,
            Long productId,
            Integer amount
    ) {}
    
    public record ReduceAvailable(
            String loginId,
            Long productId,
            Integer amount
    ) {}
    
    public record Reserve(
            String loginId,
            Long productId,
            Integer amount
    ) {}
    
    public record ReleaseReserved(
            String loginId,
            Long productId,
            Integer amount
    ) {}
}