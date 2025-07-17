package com.loopers.domain.user;

import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserServiceIntegrationTest extends IntegrationTest {
    /**
     * - [x]회원 가입시 User 저장이 수행된다.
     * - [x]이미 가입된 ID 로 회원가입 시도 시, 실패한다.
     *
     * - [x]해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.
     * - [x]해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
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
                "2025-01-01",
                0L
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
                "2025-01-01",
                0L
        );
        userService.signUp(userCommand);

        // when & then
        assertThrows(CoreException.class, () ->
                userService.signUp(userCommand));
    }

    @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
    @Test
    void returnsUserInformation_whenUserExists() {
        // given
        final String loginId = "test123456";
        UserCommand.SignUp userCommand = new UserCommand.SignUp(
                loginId,
                "test",
                "F",
                "test@example.com",
                "2025-01-01",
                0L
        );
        userService.signUp(userCommand);

        // when
        UserInfo userInfo = userService.getMyInfo(loginId);

        // then
        assertAll(
                () -> assertThat(userInfo).isNotNull(),
                () -> {
                    Assertions.assertNotNull(userInfo);
                    assertThat(userInfo.loginId()).isEqualTo(loginId);
                }
        );
    }

    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
    @Test
    void returnsNull_whenUserDoesNotExist() {
        // given
        final String loginId = "test123456";

        // when
        UserInfo userInfo = userService.getMyInfo(loginId);

        // then
        assertThat(userInfo).isNull();
    }
}
