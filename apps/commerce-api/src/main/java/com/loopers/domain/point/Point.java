package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "point",
        indexes = {
                @Index(name = "idx_point_user_id", columnList = "userId")
        })
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Embedded
    private Balance balance;

    @Column(nullable = false)
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

    public void validateDeductAmount(final Long amount) {
        this.balance.validateDeductAmount(amount);
    }
}
