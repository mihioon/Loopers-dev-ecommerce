package com.loopers.interfaces.api.point;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1ApiController implements PointV1ApiSpec {

    private final PointService pointService;

    @GetMapping
    public ApiResponse<PointV1ApiDto.GetPointResponse> getPoint(
            @RequestHeader("X-USER-ID") String loginId,
            PointV1ApiDto.GetPointRequest getPointRequest
    ) {
        if (loginId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로그인 ID가 누락되었습니다.");
        }

        PointInfo pointInfo = pointService.get(1L);
        if (pointInfo == null) {
            return ApiResponse.success(new PointV1ApiDto.GetPointResponse(0L));
        }

        PointV1ApiDto.GetPointResponse response = PointV1ApiDto.GetPointResponse.from(pointInfo);
        return ApiResponse.success(response);
    }

    @PostMapping("/charge")
    public ApiResponse<PointV1ApiDto.ChargePointResponse> chargePoint(
            @RequestHeader("X-USER-ID") String loginId,
            @RequestBody PointV1ApiDto.ChargePointRequest chargePointRequest
    ) {
        if (loginId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "로그인 ID가 누락되었습니다.");
        }

        PointCommand.Charge command = new PointCommand.Charge(1L, chargePointRequest.point());
        PointInfo pointInfo = pointService.charge(command);

        PointV1ApiDto.ChargePointResponse response = PointV1ApiDto.ChargePointResponse.from(pointInfo);
        return ApiResponse.success(response);
    }
}
