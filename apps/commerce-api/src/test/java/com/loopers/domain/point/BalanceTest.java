package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class BalanceTest {

    @DisplayName("금액 생성")
    @Nested
    class Create {
        @ParameterizedTest
        @ValueSource(longs = {
                -1L
        })
        @DisplayName("0 미만의 정수로 포인트를 충전 시, BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenBalanceIsLessThanZero(long balance) {
            // given

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Balance(balance);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 이상이어야 합니다."));
        }
    }

    @DisplayName("금액 충전")
    @Nested
    class Charge {
        @ParameterizedTest
        @ValueSource(longs = {
                -1L,
                0L
        })
        @DisplayName("0 이하의 정수로 포인트를 충전 시, BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenBalanceIsLessThanOrEqualToZero(long balance) {
            // given
            final Balance sut = new Balance(0L);

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                sut.charge(balance);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 초과이어야 합니다."));
        }
    }
}
