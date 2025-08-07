package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

class IssuedCouponTest {

    @DisplayName("사용자가 사용 가능한 쿠폰을 사용하는 경우, 정상적으로 사용되고 isUsed가 true로 변경된다.")
    @Test
    void use_Success() {
        // given
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), 10L);
        ZonedDateTime futureDate = ZonedDateTime.now().plusDays(10);
        IssuedCoupon issuedCoupon = new IssuedCoupon(coupon, 100L, futureDate);

        // when
        issuedCoupon.use();

        // then
        assertThat(issuedCoupon.isUsed()).isTrue();
    }

    @DisplayName("사용자가 이미 사용된 쿠폰을 사용하는 경우, BAD_REQUEST 예외가 발생한다")
    @Test
    void use_ThrowsExceptionWhenAlreadyUsed() {
        // given
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), 10L);
        ZonedDateTime futureDate = ZonedDateTime.now().plusDays(10);
        IssuedCoupon issuedCoupon = new IssuedCoupon(coupon, 100L, futureDate);
        issuedCoupon.use();

        // when
        CoreException exception = assertThrows(CoreException.class, issuedCoupon::use);

        // then
        assertThat(exception)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "이미 사용된 쿠폰입니다."));
    }

    @DisplayName("사용자가 만료된 쿠폰을 사용하는 경우, BAD_REQUEST 예외가 발생한다")
    @Test
    void use_ThrowsExceptionWhenExpired() {
        // given
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), 10L);
        ZonedDateTime pastDate = ZonedDateTime.now().minusDays(1);
        IssuedCoupon issuedCoupon = new IssuedCoupon(coupon, 100L, pastDate);

        // when
        CoreException exception = assertThrows(CoreException.class, issuedCoupon::use);

        // then
        assertThat(exception)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "만료된 쿠폰입니다."));
    }
}
