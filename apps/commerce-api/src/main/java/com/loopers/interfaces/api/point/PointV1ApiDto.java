package com.loopers.interfaces.api.point;

public class PointV1ApiDto {
    public record GetPointRequest (
            String userId
    ) {
    }

    public record GetPointResponse (
            Long point
    ) {
        public static GetPointResponse from(Long point) {
            return new GetPointResponse(
                    point
            );
        }
    }

    public record ChargePointRequest (
            Long point
    ) {
    }

    public record ChargePointResponse (
            Long point
    ) {
        public static ChargePointResponse from(Long point) {
            return new ChargePointResponse(
                    point
            );
        }
    }
}
