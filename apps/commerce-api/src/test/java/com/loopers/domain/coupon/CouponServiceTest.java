package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @DisplayName("발급 가능한 쿠폰 정보인 경우, 쿠폰을 사용자에게 정상적으로 발급한다")
    @Test
    void issueCouponToUser_Success() {
        // given
        Long couponId = 1L;
        Long userId = 100L;
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), 10L);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(IssuedCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CouponInfo.Issue result = couponService.issueCouponToUser(couponId, userId);

        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("존재하지 않는 쿠폰 발급 시, NOT_FOUND 예외가 발생한다")
    @Test
    void issueCouponToUser_CouponNotFound() {
        // given
        Long couponId = 999L;
        Long userId = 100L;

        // when
        CoreException exception = assertThrows(CoreException.class, () -> {
            couponService.issueCouponToUser(couponId, userId);
        });

        // when & then
        assertThat(exception)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 쿠폰입니다."));
    }

    @DisplayName("재고가 없는 쿠폰 발급 시, BAD_REQUEST 예외가 발생한다")
    @Test
    void issueCouponToUser_NoStock() {
        // given
        Long userId = 100L;
        Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("5000"), 0L);
        when(couponRepository.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        // when
        CoreException exception = assertThrows(CoreException.class, () -> {
            couponService.issueCouponToUser(coupon.getId(), userId);
        });

        // then
        assertThat(exception)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 재고가 없습니다."));
    }
}
