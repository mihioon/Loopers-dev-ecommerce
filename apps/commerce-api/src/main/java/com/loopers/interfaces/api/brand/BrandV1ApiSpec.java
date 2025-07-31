package com.loopers.interfaces.api.brand;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Brand V1 API", description = "브랜드 API V1 입니다.")
public interface BrandV1ApiSpec {

    @Operation(summary = "브랜드 조회")
    ApiResponse<GetBrand.V1.Response> getBrand(@PathVariable @NotNull Long brandId);
}
