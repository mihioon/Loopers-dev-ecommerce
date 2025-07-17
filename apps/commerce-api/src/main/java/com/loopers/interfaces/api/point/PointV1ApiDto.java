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

}
