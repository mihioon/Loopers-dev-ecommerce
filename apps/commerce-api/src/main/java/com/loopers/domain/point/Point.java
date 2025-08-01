package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "point")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Embedded
    private Balance balance;

    private Long userId;

    public Point(
            final Long userId,
            final Balance balance
    ) {
        this.userId = userId;
        this.balance = balance;
    }

    public Balance getBalance() {
        return balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void charge(final Long point) {
        this.balance.charge(point);
    }

    public void deduct(final Long point) {
        this.balance.deduct(point);
    }
}
