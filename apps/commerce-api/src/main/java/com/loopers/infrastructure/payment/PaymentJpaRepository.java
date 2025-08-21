package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderUuid(String orderUuid);
    
    Optional<Payment> findByPaymentUuid(String paymentUuid);
    
    @Query("SELECT p.paymentUuid FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt BETWEEN :startTime AND :endTime")
    List<String> findPendingPaymentIds(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
