package com.loopers.domain.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderUuid(String orderUuid);
    Optional<Payment> findByPaymentUuid(String paymentUuid);
    List<String> findPendingPaymentIds(LocalDateTime startTime, LocalDateTime endTime);
}
