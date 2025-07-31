package com.loopers.interfaces.api.point;

import com.loopers.domain.point.PointInfo;

public class PointV1ApiDto {
    public record GetPointRequest (
            String userId
    ) {
    }

    public record GetPointResponse (
            Long point
    ) {
        public static GetPointResponse from(PointInfo pointInfo) {
            return new GetPointResponse(
                    pointInfo.balance().getBalance()
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
        public static ChargePointResponse from(PointInfo pointInfo) {
            return new ChargePointResponse(
                    pointInfo.balance().getBalance()
            );
        }
    }
}
