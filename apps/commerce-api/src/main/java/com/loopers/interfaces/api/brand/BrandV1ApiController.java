package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandFacade;
import com.loopers.application.brand.BrandResult;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brands")
public class BrandV1ApiController implements BrandV1ApiSpec {

    private final BrandFacade brandFacade;

    @GetMapping("/{brandId}")
    public ApiResponse<GetBrand.V1.Response> getBrand(
            @PathVariable @NotNull Long brandId
    ) {
        BrandResult result = brandFacade.getBrand(brandId);

        GetBrand.V1.Response response = GetBrand.V1.Response.from(result);
        return ApiResponse.success(response);
    }
}
