package com.loopers.application.order;

import com.loopers.domain.coupon.CouponInfo;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductStockService;
import com.loopers.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class OrderFacade {
    
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final ProductService productService;
    private final ProductStockService stockService;
    private final CouponService couponService;

    @Transactional(rollbackFor = Exception.class)
    public OrderResult.Detail createOrder(OrderCriteria.Create criteria) {
        // 상품 정보 조회 및 검증
        List<Long> productIds = criteria.toProductIds();
        List<ProductInfo.Basic> products = productService.getBasics(productIds);
        
        // 요청한 상품 수와 조회된 상품 수 비교
        if (products.size() != productIds.size()) {
            throw new com.loopers.support.error.CoreException(
                    com.loopers.support.error.ErrorType.NOT_FOUND, 
                    "상품을 찾을 수 없습니다."
            );
        }
        
        Map<Long, ProductInfo.Basic> productMap = products.stream()
                .collect(java.util.stream.Collectors.toMap(ProductInfo.Basic::id, p -> p));

        // 주문 아이템 목록 생성 및 가격 검증
        List<OrderItem> orderItems = criteria.toOrderItems(productMap);

        // 쿠폰 할인 계산
        List<Long> couponIds = criteria.couponIds();
        List<CouponInfo.Discount> couponDiscounts = couponService.getByIds(couponIds, criteria.userId());
        BigDecimal couponDiscountAmount = couponService.calculateTotalAmount(couponDiscounts);
        
        // 최종 금액 계산
        BigDecimal totalAmount = orderService.calculateTotalAmount(orderItems, couponDiscountAmount, criteria.pointAmount());

        // 재고 차감
        stockService.validateAndReduceStocks(criteria.toStockReduceCommands());

        // 쿠폰 사용
        if (criteria.couponIds() != null && !criteria.couponIds().isEmpty()) {
            criteria.couponIds().forEach(couponService::useIssuedCoupon);
        }

        // 포인트 차감
        if (!(criteria.pointAmount() == null) && criteria.pointAmount().compareTo(BigDecimal.ZERO) > 0) {
            pointService.deduct(new PointCommand.Deduct(criteria.userId(), criteria.pointAmount().longValue()));
        }

        // 결제
        paymentService.processPayment(criteria.toPaymentCommand(totalAmount));

        // 주문 생성
        OrderInfo.Detail orderInfo = orderService.createOrder(criteria.toCommand(), orderItems);

        return OrderResult.Detail.from(orderInfo);
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
