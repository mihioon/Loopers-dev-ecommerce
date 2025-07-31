package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

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

    @DisplayName("금액 차감")
    @Nested
    class Deduct {
        
        @Test
        @DisplayName("정상적으로 포인트를 차감한다.")
        void deductPoints_Success() {
            // given
            final Balance balance = new Balance(10000L);
            final Long deductAmount = 5000L;

            // when
            balance.deduct(deductAmount);

            // then
            assertThat(balance.getBalance()).isEqualTo(5000L);
        }

        @Test
        @DisplayName("null 금액으로 차감 시 BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenAmountIsNull() {
            // given
            final Balance balance = new Balance(10000L);

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                balance.deduct(null);
            });

            // then
            assertThat(actual.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(actual.getMessage()).isEqualTo("차감 금액은 null일 수 없습니다.");
        }

        @Test
        @DisplayName("0 이하의 금액으로 차감 시 BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenAmountIsZeroOrNegative() {
            // given
            final Balance balance = new Balance(10000L);
            final Long zeroAmount = 0L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                balance.deduct(zeroAmount);
            });

            // then
            assertThat(actual.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(actual.getMessage()).isEqualTo("차감 금액은 0보다 커야 합니다.");
        }

        @Test
        @DisplayName("음수 금액으로 차감 시 BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenAmountIsNegative() {
            // given
            final Balance balance = new Balance(10000L);
            final Long negativeAmount = -1000L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                balance.deduct(negativeAmount);
            });

            // then
            assertThat(actual.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(actual.getMessage()).isEqualTo("차감 금액은 0보다 커야 합니다.");
        }

        @Test
        @DisplayName("잔액보다 많은 금액 차감 시 BAD_REQUEST 예외가 발생한다.")
        void throwsBadRequestException_whenAmountExceedsBalance() {
            // given
            final Balance balance = new Balance(5000L);
            final Long excessiveAmount = 10000L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                balance.deduct(excessiveAmount);
            });

            // then
            assertThat(actual.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(actual.getMessage()).isEqualTo("잔액이 부족합니다.");
        }

        @Test
        @DisplayName("전체 금액을 차감하면 잔액이 0이 된다.")
        void deductEntireBalance_ResultsInZero() {
            // given
            final Balance balance = new Balance(3000L);
            final Long entireAmount = 3000L;

            // when
            balance.deduct(entireAmount);

            // then
            assertThat(balance.getBalance()).isEqualTo(0L);
        }

        @Test
        @DisplayName("소수점이 있는 금액도 정상적으로 차감된다.")
        void deductDecimalAmount_Success() {
            // given
            final Balance balance = new Balance(10000L);
            final Long decimalAmount = 1500L;

            // when
            balance.deduct(decimalAmount);

            // then
            assertThat(balance.getBalance()).isEqualTo(8500L); // 10000 - 1500 (소수점 이하 버림)
        }
    }
}
