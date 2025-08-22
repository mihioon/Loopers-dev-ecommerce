package com.loopers.infrastructure.external.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentGatewayAdapter {
    
    private final CustomPaymentsClient customPaymentsClient;
    
    public PaymentGatewayInfo.ProcessResult processPayment(PaymentGatewayPayload.Process command, String userId) {
        PaymentGatewayInfo.ProcessResult result = customPaymentsClient.processPayment(userId, command);
        
        if ("FAIL".equals(result.meta().result())) {
            throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, result.meta().message());
        }
        
        return result;
    }
    
    
    public PaymentGatewayInfo.PaymentDetail getPayment(String transactionId, String userId) {
        PaymentGatewayInfo.PaymentDetail result = customPaymentsClient.getPayment(transactionId, userId);
        
        if ("FAIL".equals(result.meta().result())) {
            throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, result.meta().message());
        }
        
        return result;
    }
    
    
    public PaymentGatewayInfo.OrderPayments getPaymentsByOrderId(String orderId, String userId) {
        PaymentGatewayInfo.OrderPayments result = customPaymentsClient.getPaymentsByOrderId(orderId, userId);
        
        if ("FAIL".equals(result.meta().result())) {
            throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, result.meta().message());
        }
        
        return result;
    }
}
