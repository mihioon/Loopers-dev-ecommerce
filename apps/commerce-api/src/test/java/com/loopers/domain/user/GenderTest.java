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

public class GenderTest {
    @DisplayName("생성할 때")
    @Nested
    class Create {
        @DisplayName("성별이 유효하지 않은 값인 경우, BAD_REQUEST 예외가 발생한다.")
        @NullAndEmptySource
        @ParameterizedTest
        @ValueSource(strings = {"A"})
        void throwsBadRequestException_whenGenderIsInvalid(final String gender) {
            // given

            // when
            final CoreException actual = assertThrows(CoreException.class, () ->
                    Gender.from(gender)
            );

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "성별이 유효하지 않습니다."));
        }
    }
}
