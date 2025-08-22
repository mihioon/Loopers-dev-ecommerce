package com.loopers.domain.order;

import com.github.f4b6a3.ulid.UlidCreator;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OrderService {
    
    private final OrderRepository orderRepository;

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo.Detail createOrder(OrderCommand.Create command) {
        List<OrderCommand.Create.Product> products = command.products();
        Map<Long, OrderCommand.Create.Product> productMap = products.stream()
                .collect(Collectors.toMap(OrderCommand.Create.Product::id, Function.identity()));

        List<OrderItem> orderItems = command.orderItems().stream()
                .map(item -> {
                    OrderCommand.Create.Product product = productMap.get(item.productId());
                    if (product == null) {
                        throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다: " + item.productId());
                    }
                    return new OrderItem(item.productId(), item.quantity(), product.price());
                })
                .toList();

        // 포인트 차감 계산
        BigDecimal totalAmount = command.totalAmount().subtract(command.pointAmount());

        // 주문 생성
        Order order = new Order(
                command.userId(),
                UlidCreator.getUlid().toString(),
                orderItems,
                totalAmount,
                command.pointAmount(),
                command.couponIds());
        Order savedOrder = orderRepository.save(order);

        return OrderInfo.Detail.from(savedOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));

        order.complete();
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeOrderByUuid(String orderUuid) {
        Order order = orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));

        order.complete();
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderByUuid(String orderUuid) {
        Order order = orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));

        order.cancel();
    }

    @Transactional(readOnly = true)
    public OrderInfo.Detail getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));
        
        return OrderInfo.Detail.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderInfo.Detail> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderInfo.Detail::from)
                .toList();
    }

    public BigDecimal calculateTotalAmount(List<OrderCommand.Create.Item> items) {
        return items.stream()
                .map(item -> {
                    if (item.price() == null) {
                        throw new IllegalArgumentException("존재하지 않는 상품입니다: " + item.productId());
                    }
                    return item.price().multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public String getUuid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));

        return order.getOrderUuid();
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyCompleted(String orderUuid) {
        return orderRepository.findByOrderUuid(orderUuid)
                .map(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
                .orElse(false);
    }
}
