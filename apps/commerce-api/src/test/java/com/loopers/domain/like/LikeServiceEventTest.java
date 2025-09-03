package com.loopers.domain.like;

import com.loopers.domain.common.event.EventPublisher;
import com.loopers.domain.like.event.LikeAddedEvent;
import com.loopers.domain.like.event.LikeRemovedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("좋아요 서비스 이벤트 발행 테스트")
@ExtendWith(MockitoExtension.class)
class LikeServiceEventTest {

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("좋아요 추가 시 LikeAddedEvent가 발행된다")
    @Test
    void publishesLikeAddedEvent_whenLike() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeCommand.Like command = new LikeCommand.Like(productId, userId);
        
        ArgumentCaptor<LikeAddedEvent> eventCaptor = ArgumentCaptor.forClass(LikeAddedEvent.class);

        // when
        likeService.like(command);

        // then
        verify(productLikeRepository).save(any(ProductLike.class));
        verify(eventPublisher).publish(eventCaptor.capture());
        
        LikeAddedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getProductId()).isEqualTo(productId);
        assertThat(capturedEvent.getUserId()).isEqualTo(userId);
    }

    @DisplayName("좋아요 취소 시 LikeRemovedEvent가 발행된다")
    @Test
    void publishesLikeRemovedEvent_whenUnlike() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeCommand.Unlike command = new LikeCommand.Unlike(productId, userId);
        
        when(productLikeRepository.deleteByProductIdAndUserId(productId, userId))
                .thenReturn(1); // 1개 삭제
        
        ArgumentCaptor<LikeRemovedEvent> eventCaptor = ArgumentCaptor.forClass(LikeRemovedEvent.class);

        // when
        likeService.unlike(command);

        // then
        verify(productLikeRepository).deleteByProductIdAndUserId(productId, userId);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        LikeRemovedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getProductId()).isEqualTo(productId);
        assertThat(capturedEvent.getUserId()).isEqualTo(userId);
    }

    @DisplayName("좋아요 취소 시 삭제된 항목이 없으면 이벤트가 발행되지 않는다")
    @Test
    void doesNotPublishEvent_whenNoLikeToRemove() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeCommand.Unlike command = new LikeCommand.Unlike(productId, userId);
        
        when(productLikeRepository.deleteByProductIdAndUserId(productId, userId))
                .thenReturn(0); // 삭제된 항목 없음

        // when
        likeService.unlike(command);

        // then
        verify(productLikeRepository).deleteByProductIdAndUserId(productId, userId);
        verify(eventPublisher, never()).publish(any());
    }
}
