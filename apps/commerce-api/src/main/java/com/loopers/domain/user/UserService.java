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
    public UserInfo register(final UserCommand.Register command) {
        final User user = new User(
                new LoginId(command.loginId()),
                new Email(command.email()),
                new BirthDate(command.dob()),
                Gender.from(command.gender()),
                command.name()
        );

        if(userRepository.existsBy(user.getLoginId())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 ID 입니다.");
        }

        return UserInfo.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserInfo get(final Long userId) {
        return userRepository.findById(userId)
                .map(UserInfo::from)
                .orElse(null);
    }
}
