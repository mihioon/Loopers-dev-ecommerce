package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;

import java.math.BigDecimal;
import java.util.List;

public class OrderResult {
    
    public record Detail(
            Long id,
            Long userId,
            BigDecimal totalAmount,
            List<ItemInfo> items
    ) {
        public static Detail from(OrderInfo.Detail orderInfo) {
            return new Detail(
                    orderInfo.id(),
                    orderInfo.userId(),
                    orderInfo.totalAmount(),
                    orderInfo.items().stream()
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
        public static ItemInfo from(OrderInfo.ItemInfo itemInfo) {
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