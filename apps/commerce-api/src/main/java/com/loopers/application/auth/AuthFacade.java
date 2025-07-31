package com.loopers.application.auth;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthFacade {
    private final UserRepository userRepository;

    public Long getUserId(String loginIdStr) {
        LoginId loginId = new LoginId(loginIdStr);

        return userRepository.findByLoginId(loginId)
                .map(User::getId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
    }
}
