package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserResult;

public record GetUser() {
    public record V1() {
        public record Response(
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

            public static Response from(UserResult userResult) {
                return new Response(
                        userResult.loginId(),
                        userResult.name(),
                        GenderResponse.valueOf(userResult.gender()),
                        userResult.email(),
                        userResult.dob().toString()
                );
            }
        }
    }
}
