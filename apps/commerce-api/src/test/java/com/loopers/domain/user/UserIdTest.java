package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class UserIdTest {
    @DisplayName("생성할 때")
    @Nested
    class Create {
        @ParameterizedTest
        @ValueSource(strings = {
                "test",
                "testTentex",
                "test1234567890",
                "test1234567",
                "1234567890",
                "test____",
                "",
                "1",
                "A"
        })
        @DisplayName("ID가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenIdFormatIsInvalid(final String loginId) {
            // given

            // when
            final CoreException actual = assertThrows(CoreException.class, () ->
                    new LoginId(loginId)
            );

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내로 입력해주세요."));
        }
    }
}
