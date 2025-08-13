package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    private Point point;
    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = new Balance(10000L);
        point = new Point(1L, balance);
    }

    @DisplayName("포인트 생성이 정상적으로 동작한다")
    @Test
    void create_Success() {
        // given
        PointCommand.Create command = new PointCommand.Create(1L, 5000L);
        Point newPoint = new Point(1L, new Balance(5000L));
        
        given(pointRepository.save(any(Point.class))).willReturn(newPoint);

        // when
        PointInfo result = pointService.create(command);

        // then
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.fetchAmount()).isEqualTo(5000L);
        
        then(pointRepository).should().save(any(Point.class));
    }

    @DisplayName("포인트 충전이 정상적으로 동작한다")
    @Test
    void charge_Success() {
        // given
        PointCommand.Charge command = new PointCommand.Charge(1L, 3000L);
        
        given(pointRepository.findByUserIdWithLock(1L)).willReturn(Optional.of(point));

        // when
        PointInfo result = pointService.charge(command);

        // then
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.fetchAmount()).isEqualTo(13000L);
        
        then(pointRepository).should().findByUserIdWithLock(1L);
   }

    @DisplayName("존재하지 않는 사용자의 포인트 충전 시 예외가 발생한다")
    @Test
    void charge_UserNotFound() {
        // given
        PointCommand.Charge command = new PointCommand.Charge(999L, 3000L);
        
        given(pointRepository.findByUserIdWithLock(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.charge(command))
                .isInstanceOf(CoreException.class)
                .hasMessage("존재하지 않는 사용자입니다.")
                .extracting("errorType").isEqualTo(ErrorType.NOT_FOUND);
        
        then(pointRepository).should(never()).save(any(Point.class));
    }

    @DisplayName("포인트 차감이 정상적으로 동작한다")
    @Test
    void deduct_Success() {
        // given
        PointCommand.Deduct command = new PointCommand.Deduct(1L, 3000L);
        
        given(pointRepository.findByUserIdWithLock(1L)).willReturn(Optional.of(point));

        // when
        PointInfo result = pointService.deduct(command);

        // then
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.fetchAmount()).isEqualTo(7000L);
        
        then(pointRepository).should().findByUserIdWithLock(1L);
    }

    @DisplayName("존재하지 않는 사용자의 포인트 차감 시 예외가 발생한다")
    @Test
    void deduct_UserNotFound() {
        // given
        PointCommand.Deduct command = new PointCommand.Deduct(999L, 3000L);
        
        given(pointRepository.findByUserIdWithLock(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.deduct(command))
                .isInstanceOf(CoreException.class)
                .hasMessage("존재하지 않는 사용자입니다.")
                .extracting("errorType").isEqualTo(ErrorType.NOT_FOUND);
        
        then(pointRepository).should(never()).save(any(Point.class));
    }

    @DisplayName("잔액 부족 시 포인트 차감에서 예외가 발생한다")
    @Test
    void deduct_InsufficientBalance() {
        // given
        PointCommand.Deduct command = new PointCommand.Deduct(1L, 15000L);
        
        given(pointRepository.findByUserIdWithLock(1L)).willReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.deduct(command))
                .isInstanceOf(CoreException.class)
                .hasMessage("잔액이 부족합니다.")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
        
        then(pointRepository).should(never()).save(any(Point.class));
    }

    @DisplayName("포인트 조회가 정상적으로 동작한다")
    @Test
    void get_Success() {
        // given
        Long userId = 1L;
        
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));

        // when
        PointInfo result = pointService.get(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.fetchAmount()).isEqualTo(10000L);
        
        then(pointRepository).should().findByUserId(userId);
    }

    @DisplayName("존재하지 않는 사용자의 포인트 조회 시 예외가 발생한다")
    @Test
    void get_UserNotFound() {
        // given
        Long userId = 999L;
        
        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when
        PointInfo result = pointService.get(userId);
        
        // then - get 메서드는 null을 반환함 (예외를 던지지 않음)
        assertThat(result).isNull();
    }
}
