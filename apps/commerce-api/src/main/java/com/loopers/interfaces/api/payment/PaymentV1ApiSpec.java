package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Payment V1 API", description = "사용자 API V1 입니다.")
public interface PaymentV1ApiSpec {
    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    ApiResponse<ProcessPayment.V1.Response> processPayment(
            @RequestBody ProcessPayment.V1.Request request,
            @RequestHeader("X-USER-ID") String loginId
    );
    
    @Operation(summary = "결제 콜백 처리", description = "외부 결제 시스템에서 호출하는 콜백을 처리합니다.")
    ApiResponse<PaymentCallback.V1.Response> handlePaymentCallback(
            @RequestBody PaymentCallback.V1.Request request
    );
}
