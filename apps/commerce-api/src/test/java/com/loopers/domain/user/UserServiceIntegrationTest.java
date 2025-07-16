package com.loopers.domain.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

@Testcontainers
@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    /**
     * - [x]회원 가입시 User 저장이 수행된다.
     * - [x]이미 가입된 ID 로 회원가입 시도 시, 실패한다.
     */
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원 가입시 User 저장이 수행된다.")
    @Test
    void saveUser_whenSignUp() {
        // given
        final String loginId = "test123456";
        UserCommand.SignUp userCommand = new UserCommand.SignUp(
                loginId,
                "test",
                "F",
                "test@example.com",
                "2025-01-01"
        );

        // when
        userService.signUp(userCommand);

        // then
        UserEntity userEntity = userRepository.findByLoginId(loginId);
        assertAll(
                () -> assertThat(userCommand.loginId()).isEqualTo(userEntity.getLoginId())
        );

    }

    @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
    @Test
    void fail_whenSignUpWithDuplicateId() {
        // given
        UserCommand.SignUp userCommand = new UserCommand.SignUp(
                "test123456",
                "test",
                "F",
                "test@example.com",
                "2025-01-01"
        );
        userService.signUp(userCommand);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            userService.signUp(userCommand);
        });
    }
}
