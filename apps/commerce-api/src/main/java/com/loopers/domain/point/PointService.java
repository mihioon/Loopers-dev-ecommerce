package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PointService {
    private final PointRepository pointRepository;

    public PointInfo create(final PointCommand.Create command) {
        final Point point = new Point(command.userId(), new Balance(command.amount()));

        return PointInfo.from(pointRepository.save(point));
    }

    @Transactional(readOnly = true)
    public PointInfo get(final Long userId) {
        return pointRepository.findByUserId(userId)
                .map(PointInfo::from)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public PointInfo charge(PointCommand.Charge command) {
        Point point = pointRepository.findByUserId(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));

        point.charge(command.amount());

        return PointInfo.from(point);
    }
}
