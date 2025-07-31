package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class PaymentService {
    
    private final PaymentRepository paymentRepository;

    @Transactional(rollbackFor = Exception.class)
    public PaymentInfo.Detail processPayment(PaymentCommand.Process command) {
        Payment payment = new Payment(
                command.orderId(), 
                command.userId(), 
                command.amount(), 
                command.pointAmount()
        );
        payment.complete();
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentInfo.Detail.from(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentInfo.Detail getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));
        
        return PaymentInfo.Detail.from(payment);
    }
}