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
        try {
            PaymentGatewayInfo.ProcessResult result = customPaymentsClient.processPayment(userId, command);
            
            if ("FAIL".equals(result.meta().result())) {
                throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, result.meta().message());
            }
            
            return result;
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new CoreException(ErrorType.BAD_REQUEST, "결제 시스템 요청이 잘못되었습니다.");
            } else {
                throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, "결제 시스템 연동 중 오류가 발생했습니다.");
            }
        }
    }
    
    public PaymentGatewayInfo.PaymentDetail getPayment(String transactionId, String userId) {
        try {
            PaymentGatewayInfo.PaymentDetail result = customPaymentsClient.getPayment(transactionId, userId);
            
            if ("FAIL".equals(result.meta().result())) {
                throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, result.meta().message());
            }
            
            return result;
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new CoreException(ErrorType.BAD_REQUEST, "결제 조회 요청이 잘못되었습니다.");
            } else {
                throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, "결제 조회 중 오류가 발생했습니다.");
            }
        }
    }
    
    public PaymentGatewayInfo.OrderPayments getPaymentsByOrderId(String orderId, String userId) {
        try {
            PaymentGatewayInfo.OrderPayments result = customPaymentsClient.getPaymentsByOrderId(orderId, userId);
            
            if ("FAIL".equals(result.meta().result())) {
                throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, result.meta().message());
            }
            
            return result;
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new CoreException(ErrorType.BAD_REQUEST, "주문별 결제 조회 요청이 잘못되었습니다.");
            } else {
                throw new CoreException(ErrorType.EXTERNAL_SYSTEM_ERROR, "주문별 결제 조회 중 오류가 발생했습니다.");
            }
        }
    }
}
