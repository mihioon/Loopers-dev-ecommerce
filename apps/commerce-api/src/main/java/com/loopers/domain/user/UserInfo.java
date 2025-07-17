package com.loopers.domain.user;

public record UserInfo(
        String loginId,
        String name,
        String gender,
        String email,
        String dob,
        Long point
) {
    public static UserInfo fromEntity(UserEntity userEntity) {
        return new UserInfo(
                userEntity.getLoginId(),
                userEntity.getName(),
                userEntity.getGender(),
                userEntity.getEmail(),
                userEntity.getDob(),
                userEntity.getPoint()
        );
    }
}
