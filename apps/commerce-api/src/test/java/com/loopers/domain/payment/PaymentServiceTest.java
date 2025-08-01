package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentCommand.Process processCommand;

    @BeforeEach
    void setUp() {
        processCommand = new PaymentCommand.Process(
                1L,
                1L,
                new BigDecimal("15000"),
                new BigDecimal("5000")
        );
        
        payment = new Payment(
                1L,
                1L,
                new BigDecimal("15000"),
                new BigDecimal("5000")
        );
        payment.complete();
    }

    @DisplayName("결제 처리가 정상적으로 동작한다")
    @Test
    void processPayment_Success() {
        // given
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        PaymentInfo.Detail result = paymentService.processPayment(processCommand);

        // then
        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.amount()).isEqualTo(new BigDecimal("15000"));
        assertThat(result.pointAmount()).isEqualTo(new BigDecimal("5000"));
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().save(any(Payment.class));
    }

    @DisplayName("결제 조회가 정상적으로 동작한다")
    @Test
    void getPaymentByOrderId_Success() {
        // given
        Long orderId = 1L;
        
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));

        // when
        PaymentInfo.Detail result = paymentService.getPaymentByOrderId(orderId);

        // then
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.amount()).isEqualTo(new BigDecimal("15000"));
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().findByOrderId(orderId);
    }

    @DisplayName("존재하지 않는 주문의 결제 조회 시 null을 반환한다")
    @Test
    void getPaymentByOrderId_NotFound() {
        // given
        Long orderId = 999L;
        
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(orderId))
                .isInstanceOf(CoreException.class)
                .hasMessage("결제 정보를 찾을 수 없습니다.");
        
        then(paymentRepository).should().findByOrderId(orderId);
    }

    @DisplayName("포인트 사용이 없는 결제 처리가 정상적으로 동작한다")
    @Test
    void processPayment_WithoutPoint() {
        // given
        PaymentCommand.Process commandWithoutPoint = new PaymentCommand.Process(
                2L,
                2L,
                new BigDecimal("20000"),
                BigDecimal.ZERO
        );
        
        Payment paymentWithoutPoint = new Payment(
                2L,
                2L,
                new BigDecimal("20000"),
                BigDecimal.ZERO
        );
        paymentWithoutPoint.complete();
        
        given(paymentRepository.save(any(Payment.class))).willReturn(paymentWithoutPoint);

        // when
        PaymentInfo.Detail result = paymentService.processPayment(commandWithoutPoint);

        // then
        assertThat(result.orderId()).isEqualTo(2L);
        assertThat(result.userId()).isEqualTo(2L);
        assertThat(result.amount()).isEqualTo(new BigDecimal("20000"));
        assertThat(result.pointAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().save(any(Payment.class));
    }

    @DisplayName("결제 금액이 0원인 경우에도 정상적으로 처리된다")
    @Test
    void processPayment_ZeroAmount() {
        // given
        PaymentCommand.Process zeroAmountCommand = new PaymentCommand.Process(
                3L,
                3L,
                BigDecimal.ZERO,
                new BigDecimal("10000")
        );
        
        Payment zeroAmountPayment = new Payment(
                3L,
                3L,
                BigDecimal.ZERO,
                new BigDecimal("10000")
        );
        zeroAmountPayment.complete();
        
        given(paymentRepository.save(any(Payment.class))).willReturn(zeroAmountPayment);

        // when
        PaymentInfo.Detail result = paymentService.processPayment(zeroAmountCommand);

        // then
        assertThat(result.orderId()).isEqualTo(3L);
        assertThat(result.amount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.pointAmount()).isEqualTo(new BigDecimal("10000"));
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().save(any(Payment.class));
    }
}
