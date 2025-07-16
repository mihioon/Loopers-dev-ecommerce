package com.loopers.interfaces.api.user;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
