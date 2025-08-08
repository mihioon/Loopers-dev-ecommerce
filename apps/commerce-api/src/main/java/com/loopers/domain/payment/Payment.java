package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "payments")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    public Payment(Long userId, BigDecimal amount) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 필수입니다.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0 이상이어야 합니다.");
        }

        this.userId = userId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void complete() {
        if (status != PaymentStatus.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "대기 중인 결제만 완료할 수 있습니다.");
        }
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        if (status != PaymentStatus.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "대기 중인 결제만 실패 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.FAILED;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
}
