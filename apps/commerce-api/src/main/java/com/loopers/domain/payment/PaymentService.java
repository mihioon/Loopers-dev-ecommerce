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
                command.userId(),
                command.amount()
        );
        payment.complete();
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentInfo.Detail.from(savedPayment);
    }

}
