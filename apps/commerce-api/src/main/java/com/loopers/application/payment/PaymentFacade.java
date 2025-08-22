package com.loopers.application.payment;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import com.loopers.infrastructure.external.payment.PaymentGatewayAdapter;
import com.loopers.infrastructure.external.payment.PaymentGatewayInfo;
import com.loopers.support.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PaymentFacade {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;

    @Value("${custom-payments.callback-url}")
    private String callbackUrl;

    @Transactional
    public String processPayment(PaymentCriteria.Process criteria) {
        // 결제 생성
        String orderUuid = orderService.getUuid(criteria.orderId());
        PaymentInfo.Detail paymentInfo = paymentService.processPayment(criteria.toCommand(orderUuid));

        try {
            PaymentGatewayInfo.ProcessResult result = paymentGatewayAdapter.processPayment(
                    criteria.toGatewayCommand(orderUuid, callbackUrl),
                    criteria.userId().toString()
            );
            
            paymentService.updateTransactionKey(orderUuid, result.data().transactionKey());
            
            return paymentInfo.paymentId();
        } catch (CoreException e) {
            paymentService.cancelPayment(orderUuid);
            return paymentInfo.paymentId();
        }
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
