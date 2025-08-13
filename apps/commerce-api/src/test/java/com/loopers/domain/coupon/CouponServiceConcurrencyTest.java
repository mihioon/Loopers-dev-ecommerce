package com.loopers.domain.coupon;

import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("쿠폰 서비스 동시성 테스트")
class CouponServiceConcurrencyTest extends IntegrationTest {

    @Autowired
    private CouponService sut;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("동일한 사용자가 동시에 쿠폰 사용을 요청하면, 하나의 요청만 성공하고 나머지는 모두 BAD_REQUEST를 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {2, 10})
    void issueCouponToUser_ConcurrencyTest(int numberOfUsers) throws Exception {
        // given
        Coupon coupon = couponRepository.save(new Coupon(CouponType.RATE, new BigDecimal("0.1"), 1L));
        CouponInfo.Issue issuedCoupon = sut.issueCouponToUser(coupon.getId(), 1L);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 1; i <= numberOfUsers; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    sut.useIssuedCoupon(issuedCoupon.id());
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    failCount.incrementAndGet();
                }
            }, executorService);
            futures.add(future);
        }

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(10000, TimeUnit.SECONDS);

        executorService.shutdown();

        // then
        IssuedCoupon actual = couponRepository.findIssuedCouponById(issuedCoupon.id()).orElse(null);
        assertThat(actual)
                .isNotNull()
                .extracting(IssuedCoupon::isUsed).isEqualTo(true);
        
        System.out.println("successCount: " + successCount.get() + ", failCount: " + failCount.get() + ", expected success: 1, expected fail: " + (numberOfUsers - 1));
        
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(numberOfUsers - 1);
    }
}
