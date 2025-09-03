package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderInfo {
    
    public record Detail(
            Long id,
            Long userId,
            String orderUuid,
            BigDecimal pointAmount,
            BigDecimal totalAmount,
            List<ItemInfo> items,
            List<Long> couponIds,
            Order.OrderStatus status
    ) {
        public static Detail from(Order order) {
            return new Detail(
                    order.getId(),
                    order.getUserId(),
                    order.getOrderUuid(),
                    order.getPointAmount(),
                    order.getTotalAmount(),
                    order.getOrderItems().stream()
                            .map(ItemInfo::from)
                            .toList(),
                    order.getCouponIds(),
                    order.getStatus()
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
