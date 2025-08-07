package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Component
public class CouponService {
    private final CouponRepository couponRepository;

    @Transactional
    public CouponInfo.Issue issueCouponToUser(Long couponId, Long userId) {
        Coupon couponTemplate = couponRepository.findById(couponId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 쿠폰입니다."));

        if (!couponTemplate.hasStock()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 재고가 없습니다.");
        }

        couponTemplate.decreaseStock();

        IssuedCoupon issuedCoupon = new IssuedCoupon(
                couponTemplate,
                userId,
                ZonedDateTime.now().plusDays(couponTemplate.getExpiresInDays())
        );

        return CouponInfo.Issue.from(couponRepository.save(issuedCoupon));
    }
}
