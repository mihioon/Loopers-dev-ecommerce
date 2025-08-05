package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like")
public class LikeV1ApiController implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @PostMapping("/products/{productId}")
    public ApiResponse<AddLike.V1.Response> likeProduct(
            @RequestHeader("X-USER-ID") String loginId,
            @NotNull @PathVariable Long productId
    ) {
        likeFacade.like(productId, loginId);
        Long likeCount = likeFacade.getLikeCount(productId);

        AddLike.V1.Response response =
                AddLike.V1.Response.of(productId, likeCount);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/products/{productId}")
    public ApiResponse<RemoveLike.V1.Response> unlikeProduct(
            @RequestHeader("X-USER-ID") String loginId,
            @NotNull @PathVariable Long productId
    ) {
        likeFacade.unlike(productId, loginId);
        Long likeCount = likeFacade.getLikeCount(productId);

        RemoveLike.V1.Response response =
            RemoveLike.V1.Response.of(productId, likeCount);
        return ApiResponse.success(response);
    }

    @GetMapping("/products")
    public ApiResponse<GetUserLikeProducts.V1.Response> getUserLikeProducts(
            @NotNull @RequestHeader("X-USER-ID") String loginId
    ) {
        GetUserLikeProducts.V1.Response response = GetUserLikeProducts.V1.Response.of(
                likeFacade.getUserLikeProducts(loginId)
        );
        return ApiResponse.success(response);
    }
}
