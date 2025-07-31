package com.loopers.application.user;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;
    private final PointService pointService;

    public UserResult register(UserCriteria.Register criteria) {
        final UserInfo userInfo = userService.register(criteria.toCommand());

        pointService.create(new PointCommand.Create(userInfo.id(), 0L));

        return UserResult.from(userInfo);
    }

    public UserResult getUser(final Long userId) {
        final UserInfo userInfo = userService.get(userId);

        if (userInfo == null || userInfo.loginId() == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }

        return UserResult.from(userInfo);
    }
}
