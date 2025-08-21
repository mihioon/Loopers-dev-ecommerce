package com.loopers.application.order;

import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductStockService;
import com.loopers.domain.coupon.CouponService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private OrderService orderService;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private PointService pointService;
    
    @Mock
    private ProductService productService;
    
    @Mock
    private ProductStockService stockService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderFacade orderFacade;

    private OrderCriteria.Create criteria;
    private ProductInfo.Basic productInfo;
    private OrderInfo.Detail orderInfo;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        Long productId = 1L;
        BigDecimal price = new BigDecimal("10000");
        
        criteria = new OrderCriteria.Create(
                userId,
                List.of(new OrderCriteria.Create.Item(productId, 2)),
                BigDecimal.ZERO,
                List.of()
        );
        
        productInfo = new ProductInfo.Basic(productId, "테스트 상품", price);
        
        
        orderInfo = new OrderInfo.Detail(
                1L,
                userId,
                new BigDecimal("20000"),
                List.of(new OrderInfo.ItemInfo(1L, productId, 2, price, new BigDecimal("20000")))
        );
    }

    @DisplayName("포인트 사용량이 0보다 클 때 포인트 차감이 호출된다")
    @Test
    void placeOrder_WithPointDeduction() {
        // given
        OrderCriteria.Create criteriaWithPoint = new OrderCriteria.Create(
                1L,
                List.of(new OrderCriteria.Create.Item(1L, 2)),
                new BigDecimal("5000"),
                List.of()
        );
        
        OrderInfo.Detail orderInfoWithPoint = new OrderInfo.Detail(
                1L,
                1L,
                new BigDecimal("20000"),
                List.of(new OrderInfo.ItemInfo(1L, 1L, 2, new BigDecimal("10000"), new BigDecimal("20000")))
        );
        
        given(productService.getBasics(anyList())).willReturn(List.of(productInfo));
        given(couponService.discountProducts(anyLong(), anyList(), any(BigDecimal.class))).willReturn(new BigDecimal("20000"));
        given(paymentService.processPayment(any(PaymentCommand.Process.class)))
                .willReturn(new com.loopers.domain.payment.PaymentInfo.Detail(1L, 1L, new BigDecimal("15000"), com.loopers.domain.payment.Payment.PaymentStatus.COMPLETED));
        given(orderService.createOrder(any(OrderCommand.Create.class), anyList()))
                .willReturn(orderInfoWithPoint);

        // when
        OrderResult.Detail result = orderFacade.placeOrder(criteriaWithPoint);

        // then
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("20000"));
        
        then(pointService).should().deduct(any(PointCommand.Deduct.class));
        then(paymentService).should().processPayment(any(PaymentCommand.Process.class));
        then(stockService).should().validateAndReduceStocks(anyList());
    }

    @DisplayName("재고가 부족할 때 예외가 발생한다")
    @Test
    void placeOrder_InsufficientStock() {
        // given
        
        given(productService.getBasics(anyList())).willReturn(List.of(productInfo));
        given(couponService.discountProducts(anyLong(), anyList(), any(BigDecimal.class))).willReturn(new BigDecimal("20000"));
        given(stockService.validateAndReduceStocks(anyList())).willThrow(
                new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.")
        );

        // when & then
        assertThatThrownBy(() -> orderFacade.placeOrder(criteria))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("재고가 부족")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
        
        then(stockService).should().validateAndReduceStocks(anyList());
        then(orderService).should(never()).createOrder(any(), anyList());
    }

    @DisplayName("주문 조회가 정상적으로 동작한다")
    @Test
    void getOrder_Success() {
        // given
        Long orderId = 1L;
        given(orderService.getOrder(orderId)).willReturn(orderInfo);

        // when
        OrderResult.Detail result = orderFacade.getOrder(orderId);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(1L);
        then(orderService).should().getOrder(orderId);
    }

    @DisplayName("사용자 주문 목록 조회가 정상적으로 동작한다")
    @Test
    void getUserOrders_Success() {
        // given
        Long userId = 1L;
        List<OrderInfo.Detail> orderInfos = List.of(orderInfo);
        given(orderService.getUserOrders(userId)).willReturn(orderInfos);

        // when
        List<OrderResult.Detail> results = orderFacade.getUserOrders(userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().userId()).isEqualTo(userId);
        then(orderService).should().getUserOrders(userId);
    }
}
