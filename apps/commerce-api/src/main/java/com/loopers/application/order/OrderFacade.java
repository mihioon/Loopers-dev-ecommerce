package com.loopers.application.order;

import com.loopers.domain.common.event.EventPublisher;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.order.event.OrderCompletedEvent;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductStockService;
import com.loopers.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OrderFacade {
    private final OrderService orderService;
    private final PointService pointService;
    private final ProductService productService;
    private final ProductStockService stockService;
    private final CouponService couponService;
    private final EventPublisher eventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public OrderResult.Detail placeOrder(OrderCriteria.Create criteria) {
        // 포인트 확인
        pointService.validatePoint(criteria.userId(), criteria.pointAmount());
        // 상품 조회 및 확인
        List<ProductInfo.Basic> products = productService.getBasics(criteria.toProductIds());
        stockService.validateStocks(criteria.toStockCommands(products));
        // 주문 상품 가격 합계 계산
        BigDecimal totalAmount = orderService.calculateTotalAmount(criteria.toOrderItems(products));
        // 쿠폰 할인 금액 계산
        BigDecimal orderAmount = couponService.discountAmount(criteria.userId(), criteria.couponIds(), totalAmount);
        // 주문 생성
        OrderInfo.Detail orderInfo = orderService.createOrder(criteria.toCommand(products, orderAmount));

        // 주문 생성 이벤트 발행
        publishOrderCreatedEvent(orderInfo);

        return OrderResult.Detail.from(orderInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(OrderCriteria.Complete criteria) {
        // 주문 조회
        OrderInfo.Detail orderInfo = orderService.getOrder(criteria.orderId());
        if (orderService.isAlreadyCompleted(orderInfo.orderUuid())) {
            return;
        }

        // 포인트 차감
        pointService.deduct(criteria.toPointDeductCommand(orderInfo.pointAmount()));
        // 쿠폰 사용
        couponService.useCoupons(criteria.userId(), orderInfo.couponIds());
        // 재고 차감
        stockService.validateAndReduceStocks(criteria.toStockReduceCommands(orderInfo.items()));
        // 주문 상태 변경
        orderService.completeOrder(criteria.orderId());

        // 주문 완료 이벤트 발행
        publishOrderCompletedEvent(orderInfo);
    }

    public OrderResult.Detail getOrder(Long orderId) {
        OrderInfo.Detail orderInfo = orderService.getOrder(orderId);
        return OrderResult.Detail.from(orderInfo);
    }

    public List<OrderResult.Detail> getUserOrders(Long userId) {
        List<OrderInfo.Detail> orderInfos = orderService.getUserOrders(userId);
        return orderInfos.stream()
                .map(OrderResult.Detail::from)
                .toList();
    }

    private void publishOrderCreatedEvent(OrderInfo.Detail orderInfo) {
        List<OrderCreatedEvent.OrderItemInfo> orderItemInfos = orderInfo.items().stream()
                .map(item -> new OrderCreatedEvent.OrderItemInfo(
                        item.productId(), 
                        item.quantity(), 
                        item.price()))
                .collect(Collectors.toList());

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderInfo.id(),
                orderInfo.userId(),
                orderInfo.totalAmount(),
                orderItemInfos,
                orderInfo.couponIds(),
                orderInfo.pointAmount()
        );
        
        eventPublisher.publish(orderCreatedEvent);
    }

    private void publishOrderCompletedEvent(OrderInfo.Detail orderInfo) {
        List<OrderCompletedEvent.OrderItemInfo> orderItemInfos = orderInfo.items().stream()
                .map(item -> new OrderCompletedEvent.OrderItemInfo(
                        item.productId(), 
                        item.quantity(), 
                        item.price()))
                .collect(Collectors.toList());

        OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(
                orderInfo.id(),
                orderInfo.userId(),
                orderInfo.totalAmount(),
                orderItemInfos,
                orderInfo.couponIds(),
                orderInfo.pointAmount()
        );
        
        eventPublisher.publish(orderCompletedEvent);
    }
}
