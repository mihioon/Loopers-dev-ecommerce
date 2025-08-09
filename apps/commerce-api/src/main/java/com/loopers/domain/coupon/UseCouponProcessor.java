package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Component
@AllArgsConstructor
public class UseCouponProcessor {
    private final CouponRepository couponRepository;

    @Transactional
    public void useCoupon(Long issuedCouponId) {
        IssuedCoupon issuedCoupon = couponRepository.findIssuedCouponById(issuedCouponId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 쿠폰입니다."));

        issuedCoupon.use();
    }
}
