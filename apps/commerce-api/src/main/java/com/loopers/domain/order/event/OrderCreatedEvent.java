package com.loopers.domain.order.event;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedEvent extends OrderEvent {
    private final BigDecimal totalAmount;
    private final List<OrderItemInfo> orderItems;
    private final List<Long> couponIds;
    private final BigDecimal pointsToUse;

    public OrderCreatedEvent(Long orderId, Long userId, BigDecimal totalAmount, 
                           List<OrderItemInfo> orderItems, List<Long> couponIds, BigDecimal pointsToUse) {
        super(orderId, userId);
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.couponIds = couponIds;
        this.pointsToUse = pointsToUse;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItemInfo> getOrderItems() {
        return orderItems;
    }

    public List<Long> getCouponIds() {
        return couponIds;
    }

    public BigDecimal getPointsToUse() {
        return pointsToUse;
    }

    public static class OrderItemInfo {
        private final Long productId;
        private final Integer quantity;
        private final BigDecimal price;

        public OrderItemInfo(Long productId, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public Long getProductId() {
            return productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }
    }
}
