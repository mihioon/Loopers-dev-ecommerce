package com.loopers.domain.user;

public class UserCommand {
    public record SignUp(
            String loginId,
            String name,
            String gender,
            String email,
            String dob
    ) {
        public UserEntity toEntity() {
            return UserEntity.create(
                    loginId,
                    name,
                    gender,
                    email,
                    dob
            );
        }
    }

    public record UserInfo(
            String loginId,
            String name,
            String gender,
            String email,
            String dob
    ) {
        public static UserInfo fromEntity(UserEntity userEntity) {
            return new UserInfo(
                    userEntity.getLoginId(),
                    userEntity.getName(),
                    userEntity.getGender(),
                    userEntity.getEmail(),
                    userEntity.getDob()
            );
        }
    }
}
