package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    
    private final PaymentJpaRepository paymentJpaRepository;
    
    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByOrderUuid(String orderUuid) {
        return paymentJpaRepository.findByOrderUuid(orderUuid);
    }
    
    @Override
    public Optional<Payment> findByPaymentUuid(String paymentUuid) {
        return paymentJpaRepository.findByPaymentUuid(paymentUuid);
    }
    
    @Override
    public List<String> findPendingPaymentIds(LocalDateTime startTime, LocalDateTime endTime) {
        return paymentJpaRepository.findPendingPaymentIds(startTime, endTime);
    }
}
