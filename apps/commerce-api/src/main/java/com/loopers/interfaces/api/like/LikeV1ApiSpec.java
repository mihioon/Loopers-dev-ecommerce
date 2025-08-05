package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Like V1 API", description = "좋아요 API V1 입니다.")
public interface LikeV1ApiSpec {
    
    @Operation(summary = "상품 좋아요")
    ApiResponse<AddLike.V1.Response> likeProduct(
            @RequestHeader("X-USER-ID") String loginId,
            @NotNull @PathVariable Long productId
    );
    
    @Operation(summary = "상품 좋아요 취소")
    ApiResponse<RemoveLike.V1.Response> unlikeProduct(
            @RequestHeader("X-USER-ID") String loginId,
            @NotNull @PathVariable Long productId
    );

    @Operation(summary = "user 좋아요한 상품 조회")
    ApiResponse<GetUserLikeProducts.V1.Response> getUserLikeProducts(
            @NotNull @RequestHeader("X-USER-ID") String loginId
    );
}
