package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {

    @DisplayName("결제 생성")
    @Nested
    class CreatePayment {

        @DisplayName("정상적인 결제 생성 - 포인트 없음")
        @Test
        void createPayment_NoPoint_Success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            BigDecimal amount = new BigDecimal("20000");
            BigDecimal pointAmount = BigDecimal.ZERO;

            // when
            Payment payment = new Payment(userId, amount, "paymentId", orderId);

            // then
            assertThat(payment.getUserId()).isEqualTo(userId);
            assertThat(payment.getAmount()).isEqualTo(amount);
            assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
        }

        @DisplayName("정상적인 결제 생성 - 포인트 사용")
        @Test
        void createPayment_WithPoint_Success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            BigDecimal amount = new BigDecimal("15000");
            BigDecimal pointAmount = new BigDecimal("5000");

            // when
            Payment payment = new Payment(userId, amount, "paymentId", orderId);

            // then
            assertThat(payment.getUserId()).isEqualTo(userId);
            assertThat(payment.getAmount()).isEqualTo(amount);
            assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
        }

        @DisplayName("전액 포인트 결제")
        @Test
        void createPayment_FullPoint_Success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal pointAmount = new BigDecimal("20000");

            // when
            Payment payment = new Payment(userId, amount, "paymentId", orderId);

            // then
            assertThat(payment.getAmount()).isEqualTo(BigDecimal.ZERO);
            assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
        }
    }

    @DisplayName("결제 상태 변경")
    @Nested
    class PaymentStatusChange {

        @DisplayName("결제 완료 처리")
        @Test
        void completePayment_Success() {
            // given
            Payment payment = new Payment(1L, new BigDecimal("10000"), "paymentId", 1L);

            // when
            payment.complete();

            // then
            assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        }

        @DisplayName("결제 실패 처리")
        @Test
        void failPayment_Success() {
            // given
            Payment payment = new Payment(1L, new BigDecimal("10000"), "paymentId", 1L);

            // when
            payment.fail();

            // then
            assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.FAILED);
        }

        @DisplayName("이미 완료된 결제 완료 시도 시 예외")
        @Test
        void completePayment_AlreadyCompleted() {
            // given
            Payment payment = new Payment(1L, new BigDecimal("10000"), "paymentId", 1L);
            payment.complete();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                payment.complete();
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("대기 중인 결제만 완료할 수 있습니다");
        }

        @DisplayName("이미 실패한 결제 완료 시도 시 예외")
        @Test
        void completePayment_AlreadyFailed() {
            // given
            Payment payment = new Payment(1L, new BigDecimal("10000"), "paymentId", 1L);
            payment.fail();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                payment.complete();
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("대기 중인 결제만 완료할 수 있습니다");
        }

        @DisplayName("이미 완료된 결제 실패 시도 시 예외")
        @Test
        void failPayment_AlreadyCompleted() {
            // given
            Payment payment = new Payment(1L, new BigDecimal("10000"), "paymentId", 1L);
            payment.complete();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                payment.fail();
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("대기 중인 결제만 실패 처리할 수 있습니다");
        }
    }

    @DisplayName("결제 생성 실패")
    @Nested
    class CreatePaymentFailure {

        @DisplayName("사용자 ID가 null일 때 예외")
        @Test
        void createPayment_UserIdNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new Payment(null, new BigDecimal("10000"), "paymentId", 1L);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("사용자 ID는 필수입니다");
        }

        @DisplayName("결제 금액이 null일 때 예외")
        @Test
        void createPayment_AmountNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new Payment(1L, null, "paymentId", 1L);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("결제 금액은 0 이상이어야 합니다");
        }

        @DisplayName("결제 금액이 음수일 때 예외")
        @Test
        void createPayment_AmountNegative() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                new Payment(1L, new BigDecimal("-1000"), "paymentId", 1L);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("결제 금액은 0 이상이어야 합니다");
        }
    }
}
