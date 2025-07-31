package com.loopers.domain.user;

import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class UserServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private UserService sut;

    @DisplayName("유저 생성")
    @Nested
    class Create {
        @DisplayName("새로운 ID 로 회원가입 시도 시, 성공한다.")
        @Test
        void saveUser_whenSignUp() {
            // given
            final String loginIdStr = "test123456";
            UserCommand.Register command = new UserCommand.Register(loginIdStr, "test", "F", "test@example.com", "2025-01-01");

            // when
            UserInfo actual = sut.register(command);

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new UserInfo(
                            actual.id(),
                            new LoginId(loginIdStr),
                            "test",
                            Gender.F,
                            new Email("test@example.com"),
                            new BirthDate("2025-01-01")
                    ));
        }

        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, CONFLICT 예외가 발생한다.")
        @Test
        void fail_whenSignUpWithDuplicateId() {
            // given
            final String loginIdStr = "test123456";
            UserCommand.Register command = new UserCommand.Register(loginIdStr, "test", "F", "test@example.com", "2025-01-01");
            sut.register(command);

            // when
            final CoreException actual = assertThrows(CoreException.class, () ->
                    sut.register(command));

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 가입된 ID 입니다."));
        }
    }

    @DisplayName("유저 조회")
    @Nested
    class Read {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnsUserInformation_whenUserExists() {
            // given
            final String loginIdStr = "test123456";
            UserCommand.Register command = new UserCommand.Register(loginIdStr, "test", "F", "test@example.com", "2025-01-01");
            UserInfo userInfo = sut.register(command);

            // when
            UserInfo actual = sut.get(userInfo.id());

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isNotNull()
                    .isEqualTo(userInfo);
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnsNull_whenUserDoesNotExist() {
            // given
            final Long userId = 1L;

            // when
            UserInfo actual = sut.get(userId);

            // then
            assertThat(actual).isNull();
        }
    }
}
