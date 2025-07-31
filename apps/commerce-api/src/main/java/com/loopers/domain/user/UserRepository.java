package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Boolean existsBy(LoginId loginId);

    Optional<User> findById(Long userId);

    Optional<User> findByLoginId(LoginId loginId);
}
