package com.loopers.domain.point;

public record PointInfo (
    Long id,
    Long userId,
    Balance balance
){
    public static PointInfo from(final Point point) {
        return new PointInfo(
                point.getId(),
                point.getUserId(),
                point.getBalance()
        );
    }

    public Long fetchAmount() {
        return balance.getBalance();
    }
}
