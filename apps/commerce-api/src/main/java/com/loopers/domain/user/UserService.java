package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserService {
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public UserInfo signUp(UserCommand.SignUp signUpCommand) {
        if(userRepository.findByLoginId(signUpCommand.loginId()) != null) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 ID 입니다.");
        }

        UserEntity userEntity = userRepository.save(signUpCommand.toEntity());
        return UserInfo.fromEntity(userEntity);
    }

    public UserInfo getMyInfo(String loginId) {
        UserEntity userEntity = userRepository.findByLoginId(loginId);
        if (userEntity == null) {
            return null;
        }
        return UserInfo.fromEntity(userEntity);
    }

    public Long getPoint(String loginId) {
        UserEntity userEntity = userRepository.findByLoginId(loginId);
        if(userEntity == null) {
            return null;
        }

        return userRepository.findPointByLoginId(loginId);
    }

    public Long addPoint(String loginId, long point) {
        UserEntity userEntity = userRepository.findByLoginId(loginId);
        if(userEntity == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }

        userEntity.addPoint(point);
        userRepository.save(userEntity);

        return userEntity.getPoint();
    }
}
