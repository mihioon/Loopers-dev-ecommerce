package com.loopers.interfaces.api.user;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1ApiController implements UserV1ApiSpec {

    private final UserService userService;

    @PostMapping
    @Override
    public ApiResponse<UserV1ApiDto.SignUpResponse> signUp(
            @RequestBody @Valid UserV1ApiDto.SignUpRequest signUpRequest
    ) {
        UserCommand.SignUp userCommand = signUpRequest.toCommand();
        UserCommand.UserInfo userInfo = userService.signUp(userCommand);

        UserV1ApiDto.SignUpResponse response = UserV1ApiDto.SignUpResponse.from(userInfo);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<UserV1ApiDto.SignUpResponse> getMyInfo(
            @RequestHeader("X-USER-ID") String loginId
    ) {
        if (loginId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로그인 ID가 누락되었습니다.");
        }

        UserCommand.UserInfo userInfo = userService.getMyInfo(loginId);

        if (userInfo == null || userInfo.loginId() == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }

        UserV1ApiDto.SignUpResponse response = UserV1ApiDto.SignUpResponse.from(userInfo);
        return ApiResponse.success(response);

    }
}
