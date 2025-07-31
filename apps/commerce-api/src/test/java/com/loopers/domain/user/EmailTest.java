package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {
    @DisplayName("생성할 때")
    @Nested
    class Create {
        @ParameterizedTest
        @ValueSource(strings = {
                "test",
                "test.example.com",
                "test.example",
                "@example.com",
                "@com",
                "test@",
                "test@.",
                "test@..",
                "test@com",
                "test@com.",
                "test@.com",
                "test @example.com",
                "test@ example.com",
                "test@@example.com",
                ""
        })
        @NullAndEmptySource
        @DisplayName("이메일이 `xx@yy.zz` 형식에 맞지 않는 경우, BAD_REQUEST 예외가 발생한다.")
        void fail_whenEmailFormatIsInvalid(final String email) {
            // given

            // when
            CoreException actual = assertThrows(CoreException.class, () ->
                    new Email(email)
            );

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."));
        }
    }
}
