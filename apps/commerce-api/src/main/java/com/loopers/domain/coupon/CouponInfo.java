package com.loopers.domain.coupon;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CouponInfo {
    public record Issue(
            Long id,
            Long userId,
            boolean isUsed,
            CouponType type,
            BigDecimal value,
            Long expiresInDays,
            ZonedDateTime issuedAt,
            ZonedDateTime expiresAt
    ) {
        public static CouponInfo.Issue from(IssuedCoupon coupon) {
            return new CouponInfo.Issue(
                    coupon.getId(),
                    coupon.getUserId(),
                    coupon.isUsed(),
                    coupon.getCoupon().getType(),
                    coupon.getCoupon().getValue(),
                    coupon.getCoupon().getExpiresInDays(),
                    coupon.getIssuedAt(),
                    coupon.getExpiresAt()
            );
        }
    }

    public record Discount(
            Long id,
            CouponType type,
            BigDecimal value
    ) {
        public static CouponInfo.Discount from(Coupon coupon) {
            return new CouponInfo.Discount(
                    coupon.getId(),
                    coupon.getType(),
                    coupon.getValue()
            );
        }

        public static CouponInfo.Discount from(IssuedCoupon issuedCoupon) {
            return new CouponInfo.Discount(
                    issuedCoupon.getId(),
                    issuedCoupon.getCoupon().getType(),
                    issuedCoupon.getCoupon().getValue()
            );
        }
    }
}
