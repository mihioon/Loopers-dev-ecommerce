package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    @DisplayName("유저를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("성별이 null 인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenGenderIsInvalid() {
            // given
            final Gender gender = null;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new User(
                        new LoginId("test123456"),
                        new Email("test@example.com"),
                        new BirthDate("2025-01-01"),
                        gender,
                        "test"
                );
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "성별은 필수입니다."));
        }
    }
}
