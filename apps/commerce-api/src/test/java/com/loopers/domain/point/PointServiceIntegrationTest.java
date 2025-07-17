package com.loopers.domain.point;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PointServiceIntegrationTest extends IntegrationTest {
    /**
     * - [x]해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.
     * - [x]해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     * - [x]존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.
     */
    @Autowired
    private UserService pointService;

    @Autowired
    private UserService userService;

    @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
    @Test
    void returnsPoint_whenUserExists() {
        // given
        final String loginId = "test123456";
        UserCommand.SignUp userCommand = new UserCommand.SignUp(
                loginId,
                "test",
                "F",
                "test@example.com",
                "2025-01-01",
                10000L
        );
        userService.signUp(userCommand);

        // when
        Long point = pointService.getPoint(loginId);

        // then
        assertAll(
                () -> assertThat(point).isNotNull(),
                () -> assertThat(point).isEqualTo(10000L)
        );
    }

    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
    @Test
    void returnsNull_whenUserDoesNotExist() {
        // given
        final String loginId = "test123456";

        // when
        Long point = pointService.getPoint(loginId);

        // then
        assertThat(point).isNull();
    }

    @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
    @Test
    void fail_whenUserIdDoesNotExist() {
        // given
        final String loginId = "test123456";

        // when & then
        assertThrows(CoreException.class, () ->
                pointService.addPoint(loginId, 10000L));
    }


}
