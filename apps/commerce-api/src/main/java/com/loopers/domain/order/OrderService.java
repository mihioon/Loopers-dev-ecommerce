package com.loopers.domain.order;

import com.loopers.domain.coupon.CouponInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderService {
    
    private final OrderRepository orderRepository;

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo.Detail createOrder(OrderCommand.Create command, List<OrderItem> validatedItems) {
        Order order = new Order(command.userId(), command.paymentId(), validatedItems);
        Order savedOrder = orderRepository.save(order);
        return OrderInfo.Detail.from(savedOrder, command.pointAmount());
    }

    @Transactional(readOnly = true)
    public OrderInfo.Detail getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));
        
        return OrderInfo.Detail.from(order, BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public List<OrderInfo.Detail> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> OrderInfo.Detail.from(order, BigDecimal.ZERO))
                .toList();
    }

    public BigDecimal calculateTotalAmount(List<OrderItem> orderItems, BigDecimal couponDiscount, BigDecimal pointAmount) {
        BigDecimal itemsTotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal couponDiscountAmount = couponDiscount != null ? couponDiscount : BigDecimal.ZERO;
        BigDecimal pointDiscount = pointAmount != null ? pointAmount : BigDecimal.ZERO;
        
        BigDecimal totalAmount = itemsTotal.subtract(couponDiscountAmount).subtract(pointDiscount);
        
        return totalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalAmount;
    }
}
