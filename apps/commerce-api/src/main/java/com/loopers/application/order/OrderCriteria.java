package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.ProductStockCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderCriteria {
    
    public record Create(
            Long userId,
            List<Item> items,
            BigDecimal pointAmount,
            List<Long> couponIds
    ) {
        public record Item(
                Long productId,
                Integer quantity
        ) {}

        public OrderCommand.Create toCommand(Long paymentId, BigDecimal totalAmount) {
            return new OrderCommand.Create(
                    userId,
                    items.stream()
                            .map(item -> new OrderCommand.Create.Item(item.productId(), item.quantity()))
                            .toList(),
                    totalAmount,
                    paymentId
            );
        }

        public List<ProductStockCommand.Reduce> toStockReduceCommands() {
            return items.stream()
                    .map(item -> new ProductStockCommand.Reduce(item.productId(), item.quantity()))
                    .toList();
        }

        public PaymentCommand.Process toPaymentCommand(BigDecimal totalAmount) {
            return new PaymentCommand.Process(
                    userId,
                    totalAmount
            );
        }

        public List<Long> toProductIds() {
            return items.stream()
                    .map(Item::productId)
                    .toList();
        }

        public List<OrderItem> toOrderItems(Map<Long, ProductInfo.Basic> productMap) {
            return items.stream()
                    .map(item -> {
                        ProductInfo.Basic product = productMap.get(item.productId());
                        if (product == null) {
                            throw new IllegalArgumentException("존재하지 않는 상품입니다: " + item.productId());
                        }
                        return new OrderItem(item.productId(), item.quantity(), product.price());
                    })
                    .toList();
        }
    }
    
    public record Payment(
            Long userId
    ) {}
}
