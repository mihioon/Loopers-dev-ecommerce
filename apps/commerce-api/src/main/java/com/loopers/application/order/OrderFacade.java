package com.loopers.application.order;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductStockService;
import com.loopers.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {
    private final OrderService orderService;
    private final PointService pointService;
    private final ProductService productService;
    private final ProductStockService stockService;
    private final CouponService couponService;

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

        return OrderResult.Detail.from(orderInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(OrderCriteria.Complete criteria) {
        // 주문 조회
        OrderInfo.Detail orderInfo = orderService.getOrder(criteria.orderId());
        if (orderService.isAlreadyCompleted(orderInfo.orderUuid())) {
            throw new RuntimeException("이미 완료된 주문입니다.");
        }

        // 포인트 차감
        pointService.deduct(criteria.toPointDeductCommand(orderInfo.pointAmount()));
        // 쿠폰 사용
        couponService.useCoupons(criteria.userId(), orderInfo.couponIds());
        // 재고 차감
        stockService.validateAndReduceStocks(criteria.toStockReduceCommands(orderInfo.items()));
        // 주문 상태 변경
        orderService.completeOrder(criteria.orderId());
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
}
