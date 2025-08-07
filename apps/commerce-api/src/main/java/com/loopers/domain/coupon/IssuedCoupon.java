package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Table(name = "issued_coupon")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCoupon extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Long userId;

    private boolean isUsed;

    private ZonedDateTime issuedAt;

    private ZonedDateTime expiresAt;

    @Version
    private Long version;

    public IssuedCoupon(Coupon coupon, Long userId, ZonedDateTime expiresAt) {
        this.coupon = coupon;
        this.userId = userId;
        this.isUsed = false;

        ZonedDateTime now = ZonedDateTime.now();
        this.issuedAt = now;
        this.expiresAt = expiresAt;
    }

    public void use() {
        if (this.isUsed) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 사용된 쿠폰입니다.");
        }
        if (ZonedDateTime.now().isAfter(this.expiresAt)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "만료된 쿠폰입니다.");
        }
        this.isUsed = true;
    }

    public Long getUserId() {
        return userId;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public ZonedDateTime getIssuedAt() {
        return issuedAt;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public Long getVersion() {
        return version;
    }
}
