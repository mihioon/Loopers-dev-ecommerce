package com.loopers.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
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
                new BigDecimal("15000")
        );
        
        payment = new Payment(
                1L,
                new BigDecimal("15000")
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
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.amount()).isEqualTo(new BigDecimal("15000"));
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().save(any(Payment.class));
    }


    @DisplayName("포인트 사용이 없는 결제 처리가 정상적으로 동작한다")
    @Test
    void processPayment_WithoutPoint() {
        // given
        PaymentCommand.Process commandWithoutPoint = new PaymentCommand.Process(
                2L,
                new BigDecimal("20000")
        );
        
        Payment paymentWithoutPoint = new Payment(
                2L,
                new BigDecimal("20000")
        );
        paymentWithoutPoint.complete();
        
        given(paymentRepository.save(any(Payment.class))).willReturn(paymentWithoutPoint);

        // when
        PaymentInfo.Detail result = paymentService.processPayment(commandWithoutPoint);

        // then
        assertThat(result.userId()).isEqualTo(2L);
        assertThat(result.amount()).isEqualTo(new BigDecimal("20000"));
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().save(any(Payment.class));
    }

    @DisplayName("결제 금액이 0원인 경우에도 정상적으로 처리된다")
    @Test
    void processPayment_ZeroAmount() {
        // given
        PaymentCommand.Process zeroAmountCommand = new PaymentCommand.Process(
                3L,
                BigDecimal.ZERO
        );
        
        Payment zeroAmountPayment = new Payment(
                3L,
                BigDecimal.ZERO
        );
        zeroAmountPayment.complete();
        
        given(paymentRepository.save(any(Payment.class))).willReturn(zeroAmountPayment);

        // when
        PaymentInfo.Detail result = paymentService.processPayment(zeroAmountCommand);

        // then
        assertThat(result.amount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.status()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        
        then(paymentRepository).should().save(any(Payment.class));
    }
}
