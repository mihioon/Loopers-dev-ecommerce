package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1ApiController implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @Override
    @GetMapping("/")
    public ApiResponse<GetProducts.V1.Response> getProducts(
            @ModelAttribute GetProducts.V1.Request request,
            @RequestHeader(value = "X-USER-ID", required = false) String loginId
    ) {
        final ProductCriteria.Summary criteria = request.toCriteria(loginId);

        ProductResult.Summary result = productFacade.getSummary(criteria);
        GetProducts.V1.Response response = GetProducts.V1.Response.from(result);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<GetProduct.V1.Response> getProduct(
            @PathVariable @NotNull Long productId,
            @RequestHeader(value = "X-USER-ID", required = false) String loginId
    ) {
        ProductResult.Detail result = productFacade.getDetail(productId, loginId);
        GetProduct.V1.Response response = GetProduct.V1.Response.from(result);
        return ApiResponse.success(response);
    }
}
