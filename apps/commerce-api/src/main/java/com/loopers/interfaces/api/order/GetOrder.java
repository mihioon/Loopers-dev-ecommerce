package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderResult;

import java.math.BigDecimal;
import java.util.List;

public record GetOrder() {
    public record V1() {

        public record Response(
                Long orderId,
                Long userId,
                BigDecimal totalAmount,
                List<ItemInfo> items
        ) {
            public static Response from(OrderResult.Detail orderResult) {
                return new Response(
                        orderResult.id(),
                        orderResult.userId(),
                        orderResult.totalAmount(),
                        orderResult.items().stream()
                                .map(ItemInfo::from)
                                .toList()
                );
            }

            public record ItemInfo(
                    Long id,
                    Long productId,
                    Integer quantity,
                    BigDecimal price,
                    BigDecimal totalPrice
            ) {
                public static ItemInfo from(OrderResult.ItemInfo itemInfo) {
                    return new ItemInfo(
                            itemInfo.id(),
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