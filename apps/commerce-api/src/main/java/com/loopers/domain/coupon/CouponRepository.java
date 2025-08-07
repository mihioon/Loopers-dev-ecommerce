package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(Long couponId);

    IssuedCoupon save(IssuedCoupon issuedCoupon);

    Optional<IssuedCoupon> findIssuedCouponById(Long issuedCouponId);
}
