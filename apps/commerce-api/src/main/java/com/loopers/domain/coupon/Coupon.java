package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "coupon")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private Long expiresInDays;

    public Coupon(CouponType type, BigDecimal value, Long stock) {
        this.type = type;
        this.value = value;
        this.stock = stock;
        this.expiresInDays = 30L;
    }

    public boolean hasStock() {
        return this.stock > 0;
    }

    public Long getExpiresInDays() {
        return this.expiresInDays;
    }

    public void decreaseStock() {
        if (this.stock <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감 가능한 쿠폰이 없습니다.");
        }
        this.stock--;
    }

    public BigDecimal applyDiscount(BigDecimal originalAmount) {
        DiscountPolicy discountPolicy = createDiscountPolicy();
        return discountPolicy.discount(originalAmount);
    }

    private DiscountPolicy createDiscountPolicy() {
        if (this.type == CouponType.FIXED) {
            return new FixedDiscountPolicy(this.value);
        }
        if (this.type == CouponType.RATE) {
            return new RateDiscountPolicy(this.value);
        }
        throw new IllegalArgumentException("지원하지 않는 쿠폰 타입입니다.");
    }

    public CouponType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Long getStock() {
        return stock;
    }
}
