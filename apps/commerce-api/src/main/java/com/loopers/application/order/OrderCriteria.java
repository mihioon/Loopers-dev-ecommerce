package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.ProductStockCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        public record Product(
                Long id,
                String name,
                BigDecimal price
        ) {}

        public OrderCommand.Create toCommand(List<ProductInfo.Basic> products, BigDecimal totalAmount) {
            return new OrderCommand.Create(
                    userId,
                    toOrderItems(products),
                    products.stream()
                            .map(product -> new OrderCommand.Create.Product(product.id(), product.name(), product.price()))
                            .toList(),
                    totalAmount,
                    pointAmount
            );
        }

        public List<Long> toProductIds() {
            return items.stream()
                    .map(Item::productId)
                    .toList();
        }

        public List<OrderCommand.Create.Item> toOrderItems(List<ProductInfo.Basic> products) {
            Map<Long, ProductInfo.Basic> productMap = products.stream()
                    .collect(Collectors.toMap(ProductInfo.Basic::id, Function.identity()));

            return items.stream()
                    .map(item -> {
                        ProductInfo.Basic product = productMap.get(item.productId());
                        if (product != null) {
                            return new OrderCommand.Create.Item(item.productId(), item.quantity(), product.price());
                        } else {
                            return new OrderCommand.Create.Item(item.productId(), item.quantity(), null);
                        }
                    })
                    .toList();
        }

        public List<ProductStockCommand.Reduce> toStockCommands(List<ProductInfo.Basic> products) {
            return items.stream()
                    .map(item -> new ProductStockCommand.Reduce(item.productId(), item.quantity()))
                    .toList();
        }
    }

    public record Complete(
            Long orderId,
            Long userId,
            List<Long> couponIds
    ) {
        public PointCommand.Deduct toPointDeductCommand(BigDecimal pointAmount) {
            return new PointCommand.Deduct(userId, pointAmount.longValue());
        }

        public List<ProductStockCommand.Reduce> toStockReduceCommands(List<OrderInfo.ItemInfo> items) {
            return items.stream()
                    .map(item -> new ProductStockCommand.Reduce(item.productId(), item.quantity()))
                    .toList();
        }
    }
}
