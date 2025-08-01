package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

    @DisplayName("주문 상품 생성")
    @Nested
    class CreateOrderItem {

        @DisplayName("정상적인 주문 상품 생성")
        @Test
        void createOrderItem_Success() {
            // given
            Long productId = 1L;
            Integer quantity = 3;
            BigDecimal price = new BigDecimal("15000");

            // when
            OrderItem orderItem = new OrderItem(productId, quantity, price);

            // then
            assertThat(orderItem.getProductId()).isEqualTo(productId);
            assertThat(orderItem.getQuantity()).isEqualTo(quantity);
            assertThat(orderItem.getPrice()).isEqualTo(price);
            assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("45000"));
        }

        @DisplayName("총 가격 계산 검증")
        @Test
        void getTotalPrice_Calculation() {
            // given & when
            OrderItem item1 = new OrderItem(1L, 1, new BigDecimal("10000"));
            OrderItem item2 = new OrderItem(2L, 5, new BigDecimal("2000"));
            OrderItem item3 = new OrderItem(3L, 10, new BigDecimal("500"));

            // then
            assertThat(item1.getTotalPrice()).isEqualTo(new BigDecimal("10000"));
            assertThat(item2.getTotalPrice()).isEqualTo(new BigDecimal("10000"));
            assertThat(item3.getTotalPrice()).isEqualTo(new BigDecimal("5000"));
        }

        @DisplayName("소수점 가격 처리")
        @Test
        void createOrderItem_DecimalPrice() {
            // given
            Long productId = 1L;
            Integer quantity = 3;
            BigDecimal price = new BigDecimal("999.99");

            // when
            OrderItem orderItem = new OrderItem(productId, quantity, price);

            // then
            assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("2999.97"));
        }
    }

    @DisplayName("주문 상품 생성 실패")
    @Nested
    class CreateOrderItemFailure {

        @DisplayName("상품 ID가 null일 때 예외")
        @Test
        void createOrderItem_ProductIdNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(null, 1, new BigDecimal("10000"));
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("상품 ID는 필수입니다");
        }

        @DisplayName("수량이 null일 때 예외")
        @Test
        void createOrderItem_QuantityNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(1L, null, new BigDecimal("10000"));
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("수량은 1개 이상이어야 합니다");
        }

        @DisplayName("수량이 0일 때 예외")
        @Test
        void createOrderItem_QuantityZero() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(1L, 0, new BigDecimal("10000"));
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("수량은 1개 이상이어야 합니다");
        }

        @DisplayName("수량이 음수일 때 예외")
        @Test
        void createOrderItem_QuantityNegative() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(1L, -1, new BigDecimal("10000"));
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("수량은 1개 이상이어야 합니다");
        }

        @DisplayName("가격이 null일 때 예외")
        @Test
        void createOrderItem_PriceNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(1L, 1, null);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("가격은 0보다 커야 합니다");
        }

        @DisplayName("가격이 0일 때 예외")
        @Test
        void createOrderItem_PriceZero() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(1L, 1, BigDecimal.ZERO);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("가격은 0보다 커야 합니다");
        }

        @DisplayName("가격이 음수일 때 예외")
        @Test
        void createOrderItem_PriceNegative() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new OrderItem(1L, 1, new BigDecimal("-1000"));
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("가격은 0보다 커야 합니다");
        }
    }
}