package com.loopers.application.order;

import com.loopers.domain.product.ProductBrandService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.StockInfo;
import com.loopers.domain.product.StockService;
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
    private StockService stockService;
    
    @Mock
    private PointService pointService;
    
    @Mock
    private ProductBrandService productBrandService;

    @InjectMocks
    private OrderFacade orderFacade;

    private OrderCriteria.Create criteria;
    private ProductInfo.Basic productInfo;
    private StockInfo stockInfo;
    private OrderInfo.Detail orderInfo;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        Long productId = 1L;
        BigDecimal price = new BigDecimal("10000");
        
        criteria = new OrderCriteria.Create(
                userId,
                List.of(new OrderCriteria.Create.Item(productId, 2)),
                BigDecimal.ZERO
        );
        
        productInfo = new ProductInfo.Basic(productId, "테스트 상품", price);
        stockInfo = new StockInfo(1L, productId, 10);
        
        orderInfo = new OrderInfo.Detail(
                1L,
                userId,
                new BigDecimal("20000"),
                BigDecimal.ZERO,
                List.of(new OrderInfo.ItemInfo(1L, productId, 2, price, new BigDecimal("20000")))
        );
    }

    @DisplayName("정상 주문 생성 시 모든 서비스가 순차적으로 호출된다")
    @Test
    void createOrder_Success() {
        // given
        given(productBrandService.getBasic(anyLong())).willReturn(productInfo);
        given(orderService.createOrderWithValidatedItems(any(OrderCommand.Create.class), anyList()))
                .willReturn(orderInfo);

        // when
        OrderResult.Detail result = orderFacade.createOrder(criteria);

        // then
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("20000"));
        assertThat(result.items()).hasSize(1);
        
        then(productBrandService).should().getBasic(1L);
        then(stockService).should().validateAndReduceStocks(anyList());
        then(orderService).should().createOrderWithValidatedItems(any(OrderCommand.Create.class), anyList());
        then(pointService).should(never()).deduct(any());
        then(paymentService).should().processPayment(any(PaymentCommand.Process.class));
    }

    @DisplayName("포인트 사용량이 0보다 클 때 포인트 차감이 호출된다")
    @Test
    void createOrder_WithPointDeduction() {
        // given
        OrderCriteria.Create criteriaWithPoint = new OrderCriteria.Create(
                1L,
                List.of(new OrderCriteria.Create.Item(1L, 2)),
                new BigDecimal("5000")
        );
        
        OrderInfo.Detail orderInfoWithPoint = new OrderInfo.Detail(
                1L,
                1L,
                new BigDecimal("20000"),
                new BigDecimal("5000"),
                List.of(new OrderInfo.ItemInfo(1L, 1L, 2, new BigDecimal("10000"), new BigDecimal("20000")))
        );
        
        given(productBrandService.getBasic(anyLong())).willReturn(productInfo);
        given(orderService.createOrderWithValidatedItems(any(OrderCommand.Create.class), anyList()))
                .willReturn(orderInfoWithPoint);

        // when
        OrderResult.Detail result = orderFacade.createOrder(criteriaWithPoint);

        // then
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("20000"));
        
        then(pointService).should().deduct(any(PointCommand.Deduct.class));
        then(paymentService).should().processPayment(any(PaymentCommand.Process.class));
    }

    @DisplayName("재고가 부족할 때 예외가 발생한다")
    @Test
    void createOrder_InsufficientStock() {
        // given
        StockInfo insufficientStock = new StockInfo(1L, 1L, 1);
        
        given(productBrandService.getBasic(anyLong())).willReturn(productInfo);
        given(stockService.validateAndReduceStocks(anyList())).willThrow(
                new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.")
        );

        // when & then
        assertThatThrownBy(() -> orderFacade.createOrder(criteria))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("재고가 부족")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
        
        then(stockService).should().validateAndReduceStocks(anyList());
        then(orderService).should(never()).createOrderWithValidatedItems(any(), anyList());
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
        assertThat(results.get(0).userId()).isEqualTo(userId);
        then(orderService).should().getUserOrders(userId);
    }
}
