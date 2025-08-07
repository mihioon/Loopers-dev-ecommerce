package com.loopers.domain.coupon;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class FixedDiscountPolicy implements DiscountPolicy {
    private final BigDecimal discountAmount;

    @Override
    public BigDecimal discount(BigDecimal originalAmount) {
        return originalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
    }
}
