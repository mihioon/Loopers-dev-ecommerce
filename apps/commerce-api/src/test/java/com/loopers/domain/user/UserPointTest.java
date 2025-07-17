package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.Assert.assertThrows;

public class UserPointTest {
    /**
     * - [x]0 이하의 정수로 포인트를 충전 시 실패한다.
     */
    @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
    @ParameterizedTest
    @ValueSource(longs = {
            -1L,
            0L
    })
    void fail_whenPointIsLessThanZero(long point) {
        // given
        UserEntity userEntity = UserEntity.create("test123456", "test", "F", "test@example.com", "2025-01-01", 0L);

        // when & then
        assertThrows(CoreException.class, () -> {
            userEntity.addPoint(point);
        });
    }
}
