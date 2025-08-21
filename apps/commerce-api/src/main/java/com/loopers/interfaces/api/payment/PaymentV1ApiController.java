package com.loopers.interfaces.api.payment;

import com.loopers.application.auth.AuthFacade;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentV1ApiController implements PaymentV1ApiSpec {
    private final AuthFacade authFacade;
    private final PaymentFacade paymentFacade;

    @PostMapping
    public ApiResponse<ProcessPayment.V1.Response> processPayment(
            @RequestBody ProcessPayment.V1.Request request,
            @RequestHeader("X-USER-ID") String loginId
    ) {
        Long userId = authFacade.getUserId(loginId);
        String paymentId = paymentFacade.processPayment(request.toCriteria(userId));
        return ApiResponse.success(new ProcessPayment.V1.Response(paymentId));
    }

}
