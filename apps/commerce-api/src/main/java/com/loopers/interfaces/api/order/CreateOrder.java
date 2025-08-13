package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderResult;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrder() {
    public record V1() {

        public record Request(
                List<Item> items,
                BigDecimal pointAmount,
                List<Long> couponIds
        ) {
            public record Item(
                    Long productId,
                    Integer quantity,
                    Long couponId
            ) {}

            public OrderCriteria.Create toCriteria(Long userId) {
                return new OrderCriteria.Create(
                        userId,
                        items.stream()
                                .map(item -> new OrderCriteria.Create.Item(item.productId(), item.quantity()))
                                .toList(),
                        pointAmount != null ? pointAmount : BigDecimal.ZERO,
                        couponIds
                );
            }
        }

        public record PaymentRequest(
                Long orderId,
                BigDecimal pointAmount
        ) {}

        public record Response(
                Long orderId,
                BigDecimal totalAmount,
                List<ItemInfo> items
        ) {
            public static Response from(OrderResult.Detail orderResult) {
                return new Response(
                        orderResult.id(),
                        orderResult.totalAmount(),
                        orderResult.items().stream()
                                .map(ItemInfo::from)
                                .toList()
                );
            }

            public record ItemInfo(
                    Long productId,
                    Integer quantity,
                    BigDecimal price,
                    BigDecimal totalPrice
            ) {
                public static ItemInfo from(OrderResult.ItemInfo itemInfo) {
                    return new ItemInfo(
                            itemInfo.productId(),
                            itemInfo.quantity(),
                            itemInfo.price(),
                            itemInfo.totalPrice()
                    );
                }
            }
        }
    }
}
