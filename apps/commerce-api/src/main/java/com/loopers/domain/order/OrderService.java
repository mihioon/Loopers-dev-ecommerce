package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderService {
    
    private final OrderRepository orderRepository;

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo.Detail createOrder(OrderCommand.Create command, List<OrderItem> validatedItems) {
        Order order = new Order(command.userId(), command.paymentId(), validatedItems, command.totalAmount());
        Order savedOrder = orderRepository.save(order);
        return OrderInfo.Detail.from(savedOrder);
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
}
