package com.loopers.application.order;

import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductStockService;
import com.loopers.domain.coupon.CouponService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

    private OrderCriteria.Create createCriteria;
    private OrderCriteria.Complete completeCriteria;
    private ProductInfo.Basic productInfo;
    private OrderInfo.Detail orderInfo;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        Long productId = 1L;
        BigDecimal price = new BigDecimal("10000");

        createCriteria = new OrderCriteria.Create(
                userId,
                List.of(new OrderCriteria.Create.Item(productId, 2)),
                BigDecimal.ZERO,
                List.of()
        );

        completeCriteria = new OrderCriteria.Complete(1L, userId, List.of());
        
        productInfo = new ProductInfo.Basic(productId, "테스트 상품", price);

        orderInfo = new OrderInfo.Detail(
                1L,
                userId,
                new BigDecimal("20000"),
                new BigDecimal("10000"),
                List.of(new OrderInfo.ItemInfo(1L, productId, 2, price, new BigDecimal("20000")))
        );
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
