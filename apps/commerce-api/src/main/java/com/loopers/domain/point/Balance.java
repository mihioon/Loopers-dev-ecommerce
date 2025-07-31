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
}
