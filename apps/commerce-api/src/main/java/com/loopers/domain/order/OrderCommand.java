package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderCommand {
    
    public record Create(
            Long userId,
            List<Item> orderItems,
            List<Product> products,
            BigDecimal totalAmount,
            BigDecimal pointAmount,
            List<Long> couponIds
    ) {
        public record Item(
                Long productId,
                Integer quantity,
                BigDecimal price
        ) {}

        public record Product(
                Long id,
                String name,
                BigDecimal price
        ) {}
    }
    
    public record Payment(
            Long orderId,
            Long userId,
            BigDecimal totalAmount
    ) {}
}
