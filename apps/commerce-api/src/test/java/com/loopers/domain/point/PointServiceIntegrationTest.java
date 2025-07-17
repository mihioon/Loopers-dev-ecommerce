package com.loopers.domain.point;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Testcontainers
@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {
    /**
     * - [x]  해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.
     * - [x]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */
    @Autowired
    private UserService pointService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository pointRepository;

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


}
