package com.loopers.interfaces.api.user;

import com.loopers.application.auth.AuthFacade;
import com.loopers.application.user.UserCriteria;
import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserResult;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1ApiController implements UserV1ApiSpec {

    private final UserFacade userFacade;
    private final AuthFacade authFacade;

    @PostMapping
    @Override
    public ApiResponse<Register.V1.Response> register(
            @RequestBody @Valid Register.V1.Request registerRequest
    ) {
        UserCriteria.Register criteria = registerRequest.toCriteria();
        UserResult result = userFacade.register(criteria);

        Register.V1.Response response = Register.V1.Response.from(result);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<GetUser.V1.Response> getUser(
            @NotNull @RequestHeader("X-USER-ID") String loginId
    ) {
        Long userId = authFacade.getUserId(loginId);

        UserResult result = userFacade.getUser(userId);

        GetUser.V1.Response response = GetUser.V1.Response.from(result);
        return ApiResponse.success(response);
    }
}
