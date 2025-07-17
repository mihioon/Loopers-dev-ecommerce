package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point V1 API", description = "사용자 API V1 입니다.")
public interface PointV1ApiSpec {
    ApiResponse<PointV1ApiDto.GetPointResponse> getPoint(String loginId, PointV1ApiDto.GetPointRequest getPointRequest);
}
