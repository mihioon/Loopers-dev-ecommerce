package com.loopers.domain.product;

import com.loopers.domain.product.dto.ProductStockCommand;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 재고 서비스 동시성 테스트")
class ProductStockConcurrencyTest extends IntegrationTest {

    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("다수의 주문 요청으로 validateAndReduceStocks로 여러 상품을 동시에 차감하는 경우," +
            "요청이 성공한 횟수만큼 재고가 정확히 차감되며, 실패한 요청의 차감은 반영되지 않아야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {2, 10, 500})
    void validateAndReduceStocks_concurrentMultiProductOrders(int numberOfOrders) throws Exception {
        // given
        Long productA = 5000L;
        Long productB = 6000L;
        Integer initialStockA = 1000;
        Integer initialStockB = 500;
        Integer reduceAmountA = 2;
        Integer reduceAmountB = 1;

        productRepository.save(new ProductStock(productA, initialStockA));
        productRepository.save(new ProductStock(productB, initialStockB));

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfOrders);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfOrders; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    List<ProductStockCommand.Reduce> commands;
                    if (Math.random() < 0.5) {
                        commands = List.of(
                            new ProductStockCommand.Reduce(productA, reduceAmountA),
                            new ProductStockCommand.Reduce(productB, reduceAmountB)
                        );
                    } else {
                        commands = List.of(
                            new ProductStockCommand.Reduce(productB, reduceAmountB),
                            new ProductStockCommand.Reduce(productA, reduceAmountA)
                        );
                    }
                    productStockService.validateAndReduceStocks(commands);
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    failCount.incrementAndGet();
                    System.err.println("validateAndReduceStocks failed: " + e.getMessage());
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(30, TimeUnit.SECONDS);

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // then
        ProductStock finalStockA = productRepository.findStockByProductId(productA).orElseThrow();
        ProductStock finalStockB = productRepository.findStockByProductId(productB).orElseThrow();

        Integer expectedStockA = initialStockA - (successCount.get() * reduceAmountA);
        Integer expectedStockB = initialStockB - (successCount.get() * reduceAmountB);

        assertThat(finalStockA.getQuantity()).isEqualTo(expectedStockA);
        assertThat(finalStockB.getQuantity()).isEqualTo(expectedStockB);
        assertThat(finalStockA.getQuantity()).isGreaterThanOrEqualTo(0);
        assertThat(finalStockB.getQuantity()).isGreaterThanOrEqualTo(0);
    }

    @DisplayName("다수의 주문 요청으로 validateAndReduceStocks로 여러 상품을 동시에 차감하는 경우," +
            "요청이 성공한 횟수만큼 재고가 정확히 차감되며, 재고 부족으로 전체 주문이 실패한 경우 실패한 요청의 차감은 반영되지 않아야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {15, 25, 500})
    void validateAndReduceStocks_shouldFailEntireOrderWhenInsufficientStock(int numberOfOrders) throws Exception {
        // given
        Long productA = 7000L;
        Long productB = 8000L;
        Integer initialStockA = numberOfOrders + 40; // 충분
        Integer initialStockB = 20; // 부족
        Integer reduceAmountA = 1;
        Integer reduceAmountB = 2; // numberOfOrders * 2 > 20

        productRepository.save(new ProductStock(productA, initialStockA));
        productRepository.save(new ProductStock(productB, initialStockB));

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfOrders);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfOrders; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    List<ProductStockCommand.Reduce> commands;
                    if (Math.random() < 0.5) {
                        commands = List.of(
                                new ProductStockCommand.Reduce(productA, reduceAmountA),
                                new ProductStockCommand.Reduce(productB, reduceAmountB)
                        );
                    } else {
                        commands = List.of(
                                new ProductStockCommand.Reduce(productB, reduceAmountB),
                                new ProductStockCommand.Reduce(productA, reduceAmountA)
                        );
                    }
                    productStockService.validateAndReduceStocks(commands);
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    if (e.getMessage().contains("재고가 부족합니다")) {
                        failCount.incrementAndGet();
                    } else {
                        System.err.println("Unexpected error: " + e.getMessage());
                    }
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(30, TimeUnit.SECONDS);

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // then
        ProductStock finalStockA = productRepository.findStockByProductId(productA).orElseThrow();
        ProductStock finalStockB = productRepository.findStockByProductId(productB).orElseThrow();

        Integer expectedStockA = initialStockA - (successCount.get() * reduceAmountA);
        Integer expectedStockB = initialStockB - (successCount.get() * reduceAmountB);

        assertThat(finalStockA.getQuantity()).isEqualTo(expectedStockA);
        assertThat(finalStockB.getQuantity()).isEqualTo(expectedStockB);
        assertThat(finalStockA.getQuantity()).isGreaterThanOrEqualTo(0);
        assertThat(finalStockB.getQuantity()).isGreaterThanOrEqualTo(0);
    }
}
