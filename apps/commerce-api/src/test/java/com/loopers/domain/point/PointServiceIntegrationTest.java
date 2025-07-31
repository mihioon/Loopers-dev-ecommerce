package com.loopers.domain.point;

import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import org.junit.jupiter.api.Nested;

public class PointServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private PointService sut;

    @Autowired
    private PointRepository pointRepository;

    @Nested
    class Read {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoint_whenUserExists() {
            // given
            final Point point = pointRepository.save(new Point(1L, new Balance(10000L)));

            // when
            final PointInfo actual = sut.get(point.getUserId());

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new PointInfo(
                            point.getId(),
                            1L,
                            new Balance(10000L))
                    );
        }
    }


    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
    @Test
    void returnsNull_whenUserDoesNotExist() {
        // given

        // when
        final PointInfo actual = sut.get(1L);

        // then
        assertThat(actual).isNull();
    }

    @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
    @Test
    void fail_whenUserIdDoesNotExist() {
        // given
        final PointCommand.Charge command = new PointCommand.Charge(1L, 10000L);

        // when & then
        assertThrows(CoreException.class, () ->
                sut.charge(command));
    }


}
