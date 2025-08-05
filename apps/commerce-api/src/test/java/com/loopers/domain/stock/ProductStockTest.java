package com.loopers.domain.stock;

import com.loopers.domain.product.ProductStock;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductStockTest {

    @DisplayName("재고 차감")
    @Nested
    class ReduceStock {

        @DisplayName("충분한 재고가 있을 때 재고 차감이 성공한다.")
        @Test
        void successfullyReducesStock_whenSufficientStock() {
            // given
            final ProductStock stock = new ProductStock(1L, 100);
            final Integer reduceAmount = 50;

            // when
            stock.reduceStock(reduceAmount);

            // then
            assertThat(stock.getQuantity()).isEqualTo(50);
        }

        @DisplayName("재고가 부족할 때 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenInsufficientStock() {
            // given
            final ProductStock stock = new ProductStock(1L, 100);
            final Integer reduceAmount = 150; // 현재 재고보다 많음

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                stock.reduceStock(reduceAmount);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다. 현재: 100, 요청: 150"));
        }

        @DisplayName("차감 수량이 0 이하 또는 null일 때 BAD_REQUEST 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        void throwsBadRequestException_whenReduceAmountIsZeroOrNegative(Integer reduceAmount) {
            // given
            final ProductStock stock = new ProductStock(1L, 100);

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                stock.reduceStock(reduceAmount);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "차감할 재고 수량은 0보다 커야 합니다."));
        }

        @DisplayName("차감 수량이 null일 때 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenReduceAmountIsNull() {
            // given
            final ProductStock stock = new ProductStock(1L, 100);

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                stock.reduceStock(null);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "차감할 재고 수량은 0보다 커야 합니다."));
        }
    }

}
