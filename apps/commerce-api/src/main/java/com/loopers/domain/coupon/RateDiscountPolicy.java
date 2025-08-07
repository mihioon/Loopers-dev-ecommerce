package com.loopers.domain.coupon;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class RateDiscountPolicy implements DiscountPolicy {
    private final BigDecimal rate;

    @Override
    public BigDecimal discount(BigDecimal originalAmount) {
        BigDecimal discountAmount = originalAmount.multiply(rate);
        return originalAmount.subtract(discountAmount);
    }
}
