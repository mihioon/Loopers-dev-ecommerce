package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "User V1 API", description = "사용자 API V1 입니다.")
public interface UserV1ApiSpec {

    @Operation(summary = "회원 가입")
    ApiResponse<Register.V1.Response> register(@RequestBody @Valid Register.V1.Request registerRequest);

    @Operation(summary = "내 정보 조회")
    ApiResponse<GetUser.V1.Response> getUser(@NotNull @RequestHeader String loginId);
}
