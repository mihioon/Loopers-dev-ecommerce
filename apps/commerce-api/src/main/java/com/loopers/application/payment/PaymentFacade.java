package com.loopers.application.payment;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PaymentFacade {
    private final PaymentService paymentService;
    private final OrderService orderService;

    public String processPayment(PaymentCriteria.Process criteria) {
        // 결제 생성
        String orderUuid = orderService.getUuid(criteria.orderId());
        PaymentInfo.Detail paymentInfo = paymentService.processPayment(criteria.toCommand(orderUuid));

        // TODO - 외부 서비스 요청

        return paymentInfo.paymentId();
    }

    @Transactional
    public void processCallback(PaymentCriteria.Callback criteria) {
        if (orderService.isAlreadyCompleted(criteria.orderUuid())) {
            return;
        }

        if("SUCCESS".equals(criteria.status())) {
            paymentService.completePayment(criteria.orderUuid());
        } else if("FAILED".equals(criteria.status())) {
            paymentService.cancelPayment(criteria.orderUuid());
        }
    }
}
