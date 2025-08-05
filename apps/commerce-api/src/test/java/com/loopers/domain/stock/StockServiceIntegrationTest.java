package com.loopers.domain.stock;

import com.loopers.domain.product.*;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StockServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private StockService sut;

    @Autowired
    private ProductStockRepository productStockRepository;

    @DisplayName("재고 차감")
    @Nested
    class ReduceStock {
        
        @DisplayName("충분한 재고가 있을 때 재고 차감이 성공한다.")
        @Test
        @Transactional
        void successfullyReducesStock_whenSufficientStock() {
            // given
            final Long productId = 1L;
            final ProductStock stock = new ProductStock(productId, 100);
            productStockRepository.save(stock);
            
            final StockCommand.Reduce command = new StockCommand.Reduce(productId, 30);

            // when
            final StockInfo actual = sut.reduceStock(command);

            // then
            assertThat(actual.quantity()).isEqualTo(70);
            
            final StockInfo dbStock = sut.getStock(productId);
            assertThat(dbStock.quantity()).isEqualTo(70);
        }

        @DisplayName("재고가 부족할 때 BAD_REQUEST 예외가 발생한다.")
        @Test
        @Transactional
        void throwsBadRequestException_whenInsufficientStock() {
            // given
            final Long productId = 1L;
            final ProductStock stock = new ProductStock(productId, 50);
            productStockRepository.save(stock);
            
            final StockCommand.Reduce command = new StockCommand.Reduce(productId, 100); // 재고보다 많음

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                sut.reduceStock(command);
            });

            // then
            assertThat(actual.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(actual.getMessage()).contains("재고가 부족합니다");
            
            final StockInfo dbStock = sut.getStock(productId);
            assertThat(dbStock.quantity()).isEqualTo(50);
        }

        @DisplayName("존재하지 않는 상품의 재고 차감 시 NOT_FOUND 예외가 발생한다.")
        @Test
        @Transactional
        void throwsNotFoundException_whenProductNotFound() {
            // given
            final Long nonExistentProductId = 999L;
            final StockCommand.Reduce command = new StockCommand.Reduce(nonExistentProductId, 10);

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                sut.reduceStock(command);
            });

            // then
            assertThat(actual.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            assertThat(actual.getMessage()).isEqualTo("상품 재고를 찾을 수 없습니다.");
        }
    }

}
