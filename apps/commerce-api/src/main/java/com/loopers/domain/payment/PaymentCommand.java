package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentCommand {
    
    public record Process(
            Long userId,
            BigDecimal amount
    ) {}
}
