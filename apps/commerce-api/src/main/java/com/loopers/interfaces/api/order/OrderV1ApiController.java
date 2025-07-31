package com.loopers.interfaces.api.order;

import com.loopers.application.auth.AuthFacade;
import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1ApiController implements OrderV1ApiSpec {
    
    private final OrderFacade orderFacade;

    private final AuthFacade authFacade;

    @PostMapping
    @Override
    public ApiResponse<CreateOrder.V1.Response> createOrder(
            @RequestBody CreateOrder.V1.Request request,
            @RequestHeader("X-USER-ID") String loginId
    ) {
        Long userId = authFacade.getUserId(loginId);
        OrderCriteria.Create criteria = request.toCriteria(userId);

        // 주문 생성
        OrderResult.Detail result = orderFacade.createOrder(criteria);

        CreateOrder.V1.Response response = CreateOrder.V1.Response.from(result);
        
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    @Override
    public ApiResponse<GetOrder.V1.Response> getOrder(
            @PathVariable Long orderId
    ) {
        OrderResult.Detail result = orderFacade.getOrder(orderId);
        GetOrder.V1.Response response = GetOrder.V1.Response.from(result);
        
        return ApiResponse.success(response);
    }

    @GetMapping
    @Override
    public ApiResponse<List<GetUserOrders.V1.Response>> getUserOrders(
            @RequestHeader("X-USER-ID") String loginId
    ) {
        Long userId = authFacade.getUserId(loginId);

        List<OrderResult.Detail> results = orderFacade.getUserOrders(userId);
        List<GetUserOrders.V1.Response> responses = results.stream()
                .map(GetUserOrders.V1.Response::from)
                .toList();
        
        return ApiResponse.success(responses);
    }
}
