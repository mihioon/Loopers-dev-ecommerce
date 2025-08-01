package com.loopers.domain.point;

public class PointCommand {

    public record Create(
            Long userId,
            Long amount
    ) {
    }

    public record Charge(
            Long userId,
            Long amount
    ) {
    }

    public record Deduct(
            Long userId,
            Long amount
    ) {
    }
}
