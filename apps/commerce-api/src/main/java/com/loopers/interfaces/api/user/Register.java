package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCriteria;
import com.loopers.application.user.UserResult;

public class Register {
    public static class V1 {
        public record Request(
                String loginId,
                String name,
                String gender,
                String email,
                String dob
        ) {
            public UserCriteria.Register toCriteria() {
                return new UserCriteria.Register(
                        loginId,
                        name,
                        gender,
                        email,
                        dob
                );
            }
        }

        public record Response(
                Long id,
                String loginId,
                String name,
                String gender,
                String email,
                String dob
        ) {
            public static Response from(UserResult userResult) {
                return new Response(
                        userResult.id(),
                        userResult.loginId(),
                        userResult.name(),
                        userResult.gender(),
                        userResult.email(),
                        userResult.dob().toString()
                );
            }
        }
    }
}
