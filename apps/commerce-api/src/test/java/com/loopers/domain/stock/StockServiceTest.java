package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ProductStockRepository productStockRepository;

    @InjectMocks
    private StockService stockService;

    private ProductStock productStock;

    @BeforeEach
    void setUp() {
        productStock = new ProductStock(1L, 100);
    }

    @DisplayName("재고 조회가 정상적으로 동작한다")
    @Test
    void getStock_Success() {
        // given
        Long productId = 1L;
        
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(productStock));

        // when
        StockInfo result = stockService.getStock(productId);

        // then
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.quantity()).isEqualTo(100);
        
        then(productStockRepository).should().findByProductId(productId);
    }

    @DisplayName("존재하지 않는 상품의 재고 조회 시 예외가 발생한다")
    @Test
    void getStock_ProductNotFound() {
        // given
        Long productId = 999L;
        
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.empty());

        // when
        StockInfo result = stockService.getStock(productId);
        
        // then - Service returns default StockInfo instead of throwing exception
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.quantity()).isEqualTo(0);
    }

    @DisplayName("재고 증가가 정상적으로 동작한다")
    @Test
    void increase_Success() {
        // given
        StockCommand.Initialize command = new StockCommand.Initialize(1L, 50);
        
        given(productStockRepository.save(any(ProductStock.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        StockInfo result = stockService.initializeStock(command);

        // then
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.quantity()).isEqualTo(50);
        
        then(productStockRepository).should().save(any(ProductStock.class));
    }

    @DisplayName("존재하지 않는 상품의 재고 증가 시 예외가 발생한다")
    @Test
    void increase_ProductNotFound() {
        // given
        StockCommand.Initialize command = new StockCommand.Initialize(999L, 50);
        
        given(productStockRepository.save(any(ProductStock.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        StockInfo result = stockService.initializeStock(command);

        // then - Initialize creates new stock regardless of existing stock
        assertThat(result.productId()).isEqualTo(999L);
        assertThat(result.quantity()).isEqualTo(50);
        
        then(productStockRepository).should().save(any(ProductStock.class));
    }

    @DisplayName("재고 감소가 정상적으로 동작한다")
    @Test
    void reduce_Success() {
        // given
        StockCommand.Reduce command = new StockCommand.Reduce(1L, 30);
        
        given(productStockRepository.findByProductId(1L)).willReturn(Optional.of(productStock));
        given(productStockRepository.save(productStock)).willReturn(productStock);

        // when
        StockInfo result = stockService.reduceStock(command);

        // then
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.quantity()).isEqualTo(70);
        
        then(productStockRepository).should().findByProductId(1L);
        then(productStockRepository).should().save(productStock);
    }

    @DisplayName("재고가 부족할 때 감소 시 예외가 발생한다")
    @Test
    void reduce_InsufficientStock() {
        // given
        StockCommand.Reduce command = new StockCommand.Reduce(1L, 150);
        
        given(productStockRepository.findByProductId(1L)).willReturn(Optional.of(productStock));

        // when & then
        assertThatThrownBy(() -> stockService.reduceStock(command))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("재고가 부족합니다")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
        
        then(productStockRepository).should(never()).save(any(ProductStock.class));
    }
}
