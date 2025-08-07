package com.loopers.domain.coupon;

import java.math.BigDecimal;

public interface DiscountPolicy {
    BigDecimal discount(BigDecimal originalAmount);
}
