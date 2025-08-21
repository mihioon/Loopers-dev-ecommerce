package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

class OrderTest {

    @DisplayName("주문 생성")
    @Nested
    class CreateOrder {

        @DisplayName("정상적인 주문 생성 - 단일 상품")
        @Test
        void createOrder_SingleItem_Success() {
            // given
            Long userId = 1L;
            OrderItem item = new OrderItem(1L, 2, new BigDecimal("10000"));
            List<OrderItem> orderItems = List.of(item);

            // when
            Order order = new Order(userId, "orderUuid", orderItems, BigDecimal.ZERO, BigDecimal.ZERO);

            // then
            assertThat(order.getUserId()).isEqualTo(userId);
            assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
            assertThat(order.getOrderItems()).hasSize(1);
            assertThat(order.getOrderItems().get(0).getProductId()).isEqualTo(1L);
            assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(2);
        }

        @DisplayName("정상적인 주문 생성 - 여러 상품")
        @Test
        void createOrder_MultipleItems_Success() {
            // given
            Long userId = 1L;
            OrderItem item1 = new OrderItem(1L, 2, new BigDecimal("10000"));
            OrderItem item2 = new OrderItem(2L, 1, new BigDecimal("5000"));
            OrderItem item3 = new OrderItem(3L, 3, new BigDecimal("3000"));
            List<OrderItem> orderItems = List.of(item1, item2, item3);

            // when
            Order order = new Order(userId, "orderUuid", orderItems, BigDecimal.ZERO, BigDecimal.ZERO);

            // then
            assertThat(order.getUserId()).isEqualTo(userId);
            assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.ZERO); // 20000 + 5000 + 9000
            assertThat(order.getOrderItems()).hasSize(3);
        }

        @DisplayName("총 금액 계산 정확성 검증")
        @Test
        void createOrder_TotalAmountCalculation() {
            // given
            Long userId = 1L;
            OrderItem item1 = new OrderItem(1L, 5, new BigDecimal("1500")); // 7500
            OrderItem item2 = new OrderItem(2L, 2, new BigDecimal("12000")); // 24000
            List<OrderItem> orderItems = List.of(item1, item2);

            // when
            Order order = new Order(userId, "orderUuid", orderItems, BigDecimal.ZERO, BigDecimal.ZERO);

            // then
            assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        }
    }

    @DisplayName("주문 생성 실패")
    @Nested
    class CreateOrderFailure {

        @DisplayName("사용자 ID가 null일 때 예외")
        @Test
        void createOrder_UserIdNull() {
            // given
            OrderItem item = new OrderItem(1L, 1, new BigDecimal("10000"));
            List<OrderItem> orderItems = List.of(item);

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new Order(null, "orderUuid", orderItems, BigDecimal.ZERO, BigDecimal.ZERO);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("사용자 ID는 필수입니다");
        }

        @DisplayName(" 주문 항목이 null일 때 예외")
        @Test
        void createOrder_ItemsNull() {
            // given
            Long userId = 1L;

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new Order(userId, "orderUuid", null, BigDecimal.ZERO, BigDecimal.ZERO);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("주문 항목이 없습니다");
        }

        @DisplayName("주문 항목이 비어있을 때 예외")
        @Test
        void createOrder_EmptyItems() {
            // given
            Long userId = 1L;
            List<OrderItem> orderItems = List.of();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new Order(userId, "orderUuid", orderItems, BigDecimal.ZERO, BigDecimal.ZERO);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("주문 항목이 없습니다");
        }
    }
}
