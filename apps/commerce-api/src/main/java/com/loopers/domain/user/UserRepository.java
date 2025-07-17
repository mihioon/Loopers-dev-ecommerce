package com.loopers.domain.user;

public interface UserRepository {
    UserEntity save(UserEntity userEntity);

    UserEntity findByLoginId(String loginId);

    Long findPointByLoginId(String loginId);
}
