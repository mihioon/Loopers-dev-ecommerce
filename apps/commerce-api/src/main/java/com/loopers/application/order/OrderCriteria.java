package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;

import java.math.BigDecimal;
import java.util.List;

public class OrderCriteria {
    
    public record Create(
            Long userId,
            List<Item> items,
            BigDecimal pointAmount
    ) {
        public record Item(
                Long productId,
                Integer quantity
        ) {}

        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                    userId,
                    items.stream()
                            .map(item -> new OrderCommand.Create.Item(item.productId(), item.quantity()))
                            .toList(),
                    pointAmount != null ? pointAmount : BigDecimal.ZERO
            );
        }
    }
    
    public record Payment(
            Long orderId,
            Long userId,
            BigDecimal pointAmount
    ) {}
}
