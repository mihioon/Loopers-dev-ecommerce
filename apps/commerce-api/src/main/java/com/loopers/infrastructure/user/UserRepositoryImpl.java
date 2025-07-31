package com.loopers.infrastructure.user;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(final User userEntity) {
        return userJpaRepository.save(userEntity);
    }

    @Override
    public Boolean existsBy(final LoginId loginId) {
        return userJpaRepository.existsByLoginId(loginId);
    }

    @Override
    public Optional<User> findById(final Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findByLoginId(final LoginId loginId) {
        return userJpaRepository.findByLoginId(loginId);
    }
}
