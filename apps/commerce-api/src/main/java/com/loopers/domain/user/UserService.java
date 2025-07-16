package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserService {
    private final UserRepository userRepository;

    public UserCommand.UserInfo signUp(UserCommand.SignUp signUpCommand) {
        if(userRepository.findByLoginId(signUpCommand.loginId()) != null) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 ID 입니다.");
        }

        UserEntity userEntity = userRepository.save(signUpCommand.toEntity());
        return UserCommand.UserInfo.fromEntity(userEntity);
    }
}
