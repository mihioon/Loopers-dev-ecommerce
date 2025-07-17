package com.loopers.domain.user;

public class UserCommand {
    public record SignUp(
            String loginId,
            String name,
            String gender,
            String email,
            String dob,
            long point
    ) {
        public UserEntity toEntity() {
            return UserEntity.create(
                    loginId,
                    name,
                    gender,
                    email,
                    dob,
                    point
            );
        }
    }
}
