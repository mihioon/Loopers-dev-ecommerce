package com.loopers.domain.auth;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthService {
    
    private final UserRepository userRepository;

    public Optional<Long> resolveUserId(final String loginIdStr) {
        if (loginIdStr == null || loginIdStr.isBlank()) {
            return Optional.empty();
        }
        
        final LoginId loginId = new LoginId(loginIdStr);
        
        return userRepository.findByLoginId(loginId)
                .map(User::getId);
    }


    public Long requireUserId(String loginId) {
        return resolveUserId(loginId)
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "로그인 ID가 누락되었습니다."));
    }

}
