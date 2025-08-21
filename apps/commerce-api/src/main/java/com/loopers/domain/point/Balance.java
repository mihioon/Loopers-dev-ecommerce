package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class Balance {
    private Long amount;
    //private String currency;

    public Balance(final Long amount) {
        if(amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 이상이어야 합니다.");
        }
        this.amount = amount;
    }
    public Long getBalance() {
        return amount;
    }

    public void charge(final Long amount) {
        if(amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 초과이어야 합니다.");
        }

        this.amount += amount;
    }

    public Balance deduct(final Long amount) {
        validateDeductAmount(amount);
        
        this.amount -= amount;
        return this;
    }

    public void validateDeductAmount(final Long amount) {
        if (amount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감 금액은 null일 수 없습니다.");
        }
        if (amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감 금액은 0 이상이어야 합니다.");
        }
        if (this.amount < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잔액이 부족합니다.");
        }
    }
}
