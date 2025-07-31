package com.loopers.application.order;

import com.loopers.domain.catalog.ProductBrandService;
import com.loopers.domain.catalog.product.ProductInfo;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {
    
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final StockService stockService;
    private final PointService pointService;
    private final ProductBrandService productBrandService;

    @Transactional(rollbackFor = Exception.class)
    public OrderResult.Detail createOrder(OrderCriteria.Create criteria) {
        // 1. 상품 정보 조회 및 검증
        List<OrderItem> orderItems = criteria.items().stream()
                .map(item -> {
                    ProductInfo.Basic productInfo = productBrandService.getBasic(item.productId());
                    return new OrderItem(item.productId(), item.quantity(), productInfo.price());
                })
                .toList();

        // 2. 재고 확인 및 차감
        List<StockCommand.Reduce> reduceCommands = criteria.items().stream()
                .map(item -> {
                    // 재고 확인
                    StockInfo stockInfo = stockService.getStock(item.productId());
                    if (stockInfo.quantity() < item.quantity()) {
                        throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");
                    }
                    return new StockCommand.Reduce(item.productId(), item.quantity());
                })
                .toList();
        
        stockService.reduceAllStocks(null, reduceCommands); // orderId는 아직 없으므로 null

        // 3. 주문 생성 (이미 검증된 상품 정보와 가격으로)
        OrderCommand.Create orderCommand = criteria.toCommand();
        OrderInfo.Detail orderInfo = orderService.createOrderWithValidatedItems(orderCommand, orderItems);

        // 4. 포인트 차감 (포인트 사용량이 0보다 클 때만)
        if (orderInfo.pointAmount().compareTo(BigDecimal.ZERO) > 0) {
            pointService.deduct(new PointCommand.Deduct(
                    criteria.userId(), 
                    orderInfo.pointAmount().longValue()
            ));
        }

        // 5. 결제 처리
        BigDecimal paymentAmount = orderInfo.totalAmount().subtract(orderInfo.pointAmount());
        paymentService.processPayment(toPaymentCommand(orderInfo, paymentAmount));

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

    private PaymentCommand.Process toPaymentCommand(OrderInfo.Detail orderInfo, BigDecimal paymentAmount) {
        return new PaymentCommand.Process(
                orderInfo.id(),
                orderInfo.userId(),
                paymentAmount,
                orderInfo.pointAmount()
        );
    }
}
