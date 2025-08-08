package com.loopers.domain.point;

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

@DisplayName("포인트 서비스 동시성 테스트")
class PointServiceConcurrencyTest extends IntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("사용자가 동시에 포인트를 충전할 때, " +
            "포인트 충전 후 충전에 성공한 포인트를 정확하게 합산한 금액이 최종적으로 반영되어야 한다")
    @ParameterizedTest
    @ValueSource(ints = {2, 10})
    void concurrentCharge_shouldMaintainCorrectAmount(int numberOfThreads) throws Exception {
        // given
        Long userId = 1L;
        Long chargeAmount = 10000L;
        Long initialBalance = 0L;

        Point initialPoint = new Point(userId, new Balance(initialBalance));
        pointRepository.save(initialPoint);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);


        // when
        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PointCommand.Charge command = new PointCommand.Charge(userId, chargeAmount);
                    pointService.charge(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.err.println("Charge failed: " + e.getMessage());
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
        Point finalPoint = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(finalPoint.getBalance().getBalance()).isEqualTo(initialBalance + (successCount.get() * chargeAmount));
    }


    @DisplayName("사용자가 동시에 포인트를 차감할 때, " +
            "잔액이 부족한 상황에서도 요청에 성공한 차감만 정확하게 반영되어야 한다.")
    @ParameterizedTest  
    @ValueSource(ints = {2, 10})
    void concurrentDeduct_shouldHandleInsufficientBalance(int numberOfThreads) throws Exception {
        // given
        Long userId = 1L;
        Long deductAmount = 10000L;
        Long initialBalance = 15000L; // numberOfThreads * deductAmount > initialBalance

        Point initialPoint = new Point(userId, new Balance(initialBalance));
        pointRepository.save(initialPoint);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PointCommand.Deduct command = new PointCommand.Deduct(userId, deductAmount);
                    pointService.deduct(command);
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    if (e.getMessage().contains("잔액이 부족합니다")) {
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
        assertThat(successCount.get() + failCount.get()).isEqualTo(numberOfThreads);

        Point finalPoint = pointRepository.findByUserId(userId).orElseThrow();

        int maxPossibleDeductions = (int) (initialBalance / deductAmount);
        assertThat(successCount.get()).isLessThanOrEqualTo(maxPossibleDeductions);

        Long expectedAmount = initialBalance - (successCount.get() * deductAmount);
        assertThat(finalPoint.getBalance().getBalance()).isEqualTo(expectedAmount);

        assertThat(finalPoint.getBalance().getBalance()).isGreaterThanOrEqualTo(0L);
    }

    @DisplayName("사용자가 동시에 포인트를 충전 및 차감할 때, " +
            "포인트 충전 후 요청 성공한 포인트들을 정확하게 합산한 금액이 최종적으로 반영되어야 한다")
    @ParameterizedTest
    @ValueSource(ints = {2, 10})
    void concurrentChargeAndDeduct_shouldMaintainConsistency(int operationsPerType) throws Exception {
        // given
        Long userId = 1L;
        Long chargeAmount = 200L;
        Long deductAmount = 150L;
        Long initialBalance = 3000L;

        Point initialPoint = new Point(userId, new Balance(initialBalance));
        pointRepository.save(initialPoint);

        ExecutorService executorService = Executors.newFixedThreadPool(operationsPerType * 2);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger chargeSuccess = new AtomicInteger(0);
        AtomicInteger deductSuccess = new AtomicInteger(0);
        AtomicInteger deductFail = new AtomicInteger(0);

        // when
        for (int i = 0; i < operationsPerType; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PointCommand.Charge command = new PointCommand.Charge(userId, chargeAmount);
                    pointService.charge(command);
                    chargeSuccess.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Charge failed: " + e.getMessage());
                }
            }, executorService);
            futures.add(future);
        }

        for (int i = 0; i < operationsPerType; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PointCommand.Deduct command = new PointCommand.Deduct(userId, deductAmount);
                    pointService.deduct(command);
                    deductSuccess.incrementAndGet();
                } catch (CoreException e) {
                    if (e.getMessage().contains("잔액이 부족합니다")) {
                        deductFail.incrementAndGet();
                    }
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(60, TimeUnit.SECONDS);

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // then
        Point finalPoint = pointRepository.findByUserId(userId).orElseThrow();

        Long expectedAmount = initialBalance +
                              (chargeSuccess.get() * chargeAmount) - 
                              (deductSuccess.get() * deductAmount);
        assertThat(finalPoint.getBalance().getBalance()).isEqualTo(expectedAmount);
        
        assertThat(finalPoint.getBalance().getBalance()).isGreaterThanOrEqualTo(0L);

        assertThat(deductSuccess.get() + deductFail.get()).isEqualTo(operationsPerType);
    }
}
