package com.loopers.domain.auth;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
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
}
