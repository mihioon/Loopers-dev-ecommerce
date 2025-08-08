package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderCommand {
    
    public record Create(
            Long userId,
            List<Item> items,
            BigDecimal totalAmount,
            Long paymentId
    ) {
        public record Item(
                Long productId,
                Integer quantity
        ) {}
    }
    
    public record Payment(
            Long orderId,
            Long userId,
            BigDecimal totalAmount
    ) {}
}
