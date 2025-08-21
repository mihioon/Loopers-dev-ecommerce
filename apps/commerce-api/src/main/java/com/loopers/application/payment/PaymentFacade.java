package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentFacade {
    private final PaymentService paymentService;

    public String processPayment(PaymentCriteria.Process criteria) {
        // 결제 생성
        PaymentInfo.Detail paymentInfo = paymentService.processPayment(criteria.toCommand());
        // TODO - 외부 서비스 요청

        return paymentInfo.paymentId();
    }

    public void processCallback(PaymentCriteria.Callback criteria) {
        // TODO - 외부 서비스 응답 처리
    }
}
