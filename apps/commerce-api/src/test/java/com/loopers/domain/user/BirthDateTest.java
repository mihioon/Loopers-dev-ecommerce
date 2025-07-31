package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class BirthDateTest {
    @DisplayName("생성할 때")
    @Nested
    class Create {
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("생년월일이 null 이면, BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenDobIsNull(final String dob) {
            // given

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new BirthDate(dob);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "생년월일은 필수값입니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "19900101",
                "1990/01/01",
                "90-01-01",
                "1990-1-1",
                "abcd-ef-gh",
                "1990-01",
                "1990",
                "01-01-1990"
        })
        @DisplayName("생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenDobFormatIsInvalid(final String dob) {
            // given

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new BirthDate(dob);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다."));
        }
    }
}
