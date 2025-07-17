package com.loopers.interfaces.api.user;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import jakarta.validation.constraints.NotNull;

public class UserV1ApiDto {
    // 데이터 전달 용도 -> record
    public record SignUpRequest(
            @NotNull String loginId,
            @NotNull String name,
            @NotNull GenderRequest gender,
            @NotNull String email,
            @NotNull String dob
    ) {
        enum GenderRequest {
            M,
            F
        }

        public UserCommand.SignUp toCommand() {
            return new UserCommand.SignUp(
                    loginId,
                    name,
                    gender.toString(),
                    email,
                    dob,
                    0L
            );
        }
    }

    public record SignUpResponse(
            String loginId,
            String name,
            GenderResponse gender,
            String email,
            String dob
    ) {
        enum GenderResponse {
            M,
            F
        }

        public static SignUpResponse from(UserInfo userInfo) {
            return new SignUpResponse(
                    userInfo.loginId(),
                    userInfo.name(),
                    GenderResponse.valueOf(userInfo.gender()),
                    userInfo.email(),
                    userInfo.dob()
            );
        }
    }
}
