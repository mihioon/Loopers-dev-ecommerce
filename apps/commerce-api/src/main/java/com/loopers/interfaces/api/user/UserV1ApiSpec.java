package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "사용자 API V1 입니다.")
public interface UserV1ApiSpec {

    @Operation(summary = "회원 가입")
    ApiResponse<UserV1ApiDto.SignUpResponse> signUp(UserV1ApiDto.SignUpRequest signUpRequest);
}
