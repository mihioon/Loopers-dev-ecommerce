package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.github.f4b6a3.ulid.UlidCreator;

@RequiredArgsConstructor
@Component
public class PaymentService {
    
    private final PaymentRepository paymentRepository;

    @Transactional(rollbackFor = Exception.class)
    public PaymentInfo.Detail processPayment(PaymentCommand.Process command) {
        Payment payment = new Payment(
                command.userId(),
                command.amount(),
                UlidCreator.getUlid().toString(),
                command.orderId()
        );
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentInfo.Detail.from(savedPayment);
    }

}
