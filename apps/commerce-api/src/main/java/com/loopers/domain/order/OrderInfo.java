package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderInfo {
    
    public record Detail(
            Long id,
            Long userId,
            BigDecimal totalAmount,
            BigDecimal pointAmount,
            List<ItemInfo> items
    ) {
        public static Detail from(Order order, BigDecimal pointAmount) {
            return new Detail(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    pointAmount,
                    order.getOrderItems().stream()
                            .map(ItemInfo::from)
                            .toList()
            );
        }
    }

    public record ItemInfo(
            Long id,
            Long productId,
            Integer quantity,
            BigDecimal price,
            BigDecimal totalPrice
    ) {
        public static ItemInfo from(OrderItem orderItem) {
            return new ItemInfo(
                    orderItem.getId(),
                    orderItem.getProductId(),
                    orderItem.getQuantity(),
                    orderItem.getPrice(),
                    orderItem.getTotalPrice()
            );
        }
    }
}
