package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Product V1 API", description = "상품 API V1 입니다.")
public interface ProductV1ApiSpec {
    @Operation(summary = "상품 목록 조회")
    ApiResponse<GetProducts.V1.Response> getProducts(
            @ModelAttribute GetProducts.V1.Request request,
            @RequestHeader(value = "X-USER-ID", required = false) String loginId
    );

    @Operation(summary = "상품 조회")
    ApiResponse<GetProduct.V1.Response> getProduct(
            @PathVariable @NotNull Long productId,
            @RequestHeader(value = "X-USER-ID", required = false) String loginId
    );
}
