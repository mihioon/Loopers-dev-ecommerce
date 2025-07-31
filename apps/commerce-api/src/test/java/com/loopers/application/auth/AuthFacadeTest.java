package com.loopers.application.auth;

import com.loopers.domain.user.*;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class AuthFacadeTest extends IntegrationTest {

    @Autowired
    private AuthFacade authFacade;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인 ID 로 회원 ID 를 조회할 수 있다.")
    @Test
    void getUserId() {
        // given
        final String loginId = "test123456";
        final User user = new User(
                new LoginId(loginId),
                new Email("test@example.com"),
                new BirthDate("2025-01-01"),
                Gender.F,
                "test"
        );
        
        userRepository.save(user);
        Long userId = Objects.requireNonNull(userRepository.findByLoginId(new LoginId(loginId)).orElse(null)).getId();

        // when & then
        assertDoesNotThrow(() -> userId.equals(authFacade.getUserId(loginId)));
    }

    @DisplayName("존재하지 않는 로그인 ID 로 회원 ID 를 조회할 경우, NOT_FOUND 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"test123456"})
    void throwsNotFoundException_whenUserDoesNotExist(String loginId) {
        // given

        // when & then
        assertThat(assertThrows(CoreException.class, () -> authFacade.getUserId(loginId)))
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
    }
}
