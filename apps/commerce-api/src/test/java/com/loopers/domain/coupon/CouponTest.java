package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class CouponTest {

    @DisplayName("정액 할인 쿠폰인 경우, 정상적으로 할인을 적용한다")
    @Test
    void applyDiscount_FixedCoupon() {
        // given
        BigDecimal discountAmount = new BigDecimal("5000");
        Coupon coupon = new Coupon(CouponType.FIXED, discountAmount, 10L);
        BigDecimal originalAmount = new BigDecimal("20000");

        // when
        BigDecimal discountedAmount = coupon.applyDiscount(originalAmount);

        // then
        assertThat(discountedAmount).isEqualTo(originalAmount.subtract(discountAmount));
    }

    @DisplayName("정액 할인 쿠폰인 경우, 상품 가격을 넘는 할인 금액이라면 최소 가격인 0원으로 할인한다")
    @Test
    void applyDiscount_FixedCoupon_WithExceedingDiscount() {
        // given
        BigDecimal discountAmount = new BigDecimal("5000");
        Coupon coupon = new Coupon(CouponType.FIXED, discountAmount, 10L);
        BigDecimal originalAmount = new BigDecimal("1000");

        // when
        BigDecimal discountedAmount = coupon.applyDiscount(originalAmount);

        // then
        assertThat(discountedAmount).isEqualTo(BigDecimal.ZERO);
    }


    @DisplayName("정률 할인 쿠폰인 경우, 정상적으로 할인을 적용한다")
    @Test
    void applyDiscount_RateCoupon() {
        // given
        BigDecimal discountRate = new BigDecimal("0.1");
        Coupon coupon = new Coupon(CouponType.RATE, discountRate, 10L);
        BigDecimal originalAmount = new BigDecimal("20000");

        // when
        BigDecimal discountedAmount = coupon.applyDiscount(originalAmount);

        // then
        assertThat(discountedAmount).isEqualTo(originalAmount.multiply(BigDecimal.ONE.subtract(discountRate)));
    }

    @DisplayName("재고가 0 초과일 때, 정상적으로 재고가 차감된다")
    @ParameterizedTest
    @ValueSource(longs = {1, 10})
    void decreaseStock_Success(Long stock) {
        // given
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), stock);

        // when
        coupon.decreaseStock();

        // then
        assertThat(coupon.getStock()).isEqualTo(stock - 1L);
    }

    @DisplayName("재고가 0일 때 차감하면, BAD_REQUEST 예외가 발생한다")
    @Test
    void decreaseStock_ThrowsExceptionWhenNoStock() {
        // given
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), 0L);

        // when
        CoreException exception = assertThrows(CoreException.class, coupon::decreaseStock);

        // then
        assertThat(exception)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "차감 가능한 쿠폰이 없습니다."));
    }
}
