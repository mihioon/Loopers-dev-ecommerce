package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Order", description = "주문 관리 API")
public interface OrderV1ApiSpec {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    public ApiResponse<CreateOrder.V1.Response> createOrder(
            @RequestBody CreateOrder.V1.Request request,
            @RequestHeader("X-USER-ID") String loginId
    );

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 주문 상세 정보를 조회합니다.")
    public ApiResponse<GetOrder.V1.Response> getOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId
    );

    @Operation(summary = "사용자 주문 목록 조회", description = "로그인한 사용자의 주문 목록을 조회합니다.")
    public ApiResponse<List<GetUserOrders.V1.Response>> getUserOrders(
            @RequestHeader("X-USER-ID") String loginId
    );
}
