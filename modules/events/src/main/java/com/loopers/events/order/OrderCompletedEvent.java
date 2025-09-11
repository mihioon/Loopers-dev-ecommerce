package com.loopers.events.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderCompletedEvent extends OrderEvent {
    private final BigDecimal finalAmount;
    private final List<OrderItemInfo> orderItems;
    private final List<Long> couponIds;
    private final BigDecimal pointsUsed;

    public OrderCompletedEvent(Long orderId, Long userId, 
                             BigDecimal finalAmount, List<OrderItemInfo> orderItems,
                             List<Long> couponIds, BigDecimal pointsUsed) {
        super(orderId, userId);
        this.finalAmount = finalAmount;
        this.orderItems = orderItems;
        this.couponIds = couponIds;
        this.pointsUsed = pointsUsed;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public List<OrderItemInfo> getOrderItems() {
        return orderItems;
    }

    public List<Long> getCouponIds() {
        return couponIds;
    }

    public BigDecimal getPointsUsed() {
        return pointsUsed;
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
