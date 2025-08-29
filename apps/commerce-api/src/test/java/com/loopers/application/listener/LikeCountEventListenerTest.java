package com.loopers.application.listener;

import com.loopers.domain.like.event.LikeAddedEvent;
import com.loopers.domain.like.event.LikeRemovedEvent;
import com.loopers.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@DisplayName("좋아요 카운트 이벤트 리스너 테스트")
@ExtendWith(MockitoExtension.class)
class LikeCountEventListenerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private LikeCountEventListener likeCountEventListener;

    @DisplayName("LikeAddedEvent 수신 시 좋아요 카운트가 증가한다")
    @Test
    void increasesLikeCount_whenLikeAddedEvent() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeAddedEvent event = new LikeAddedEvent(productId, userId);

        // when
        likeCountEventListener.handleLikeAdded(event);

        // then
        verify(productService).updateStatusLikeCount(productId, true);
    }

    @DisplayName("LikeRemovedEvent 수신 시 좋아요 카운트가 감소한다")
    @Test
    void decreasesLikeCount_whenLikeRemovedEvent() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeRemovedEvent event = new LikeRemovedEvent(productId, userId);

        // when
        likeCountEventListener.handleLikeRemoved(event);

        // then
        verify(productService).updateStatusLikeCount(productId, false);
    }

    @DisplayName("LikeAddedEvent 처리 중 예외 발생 시에도 오류가 전파되지 않는다")
    @Test
    void doesNotThrowException_whenLikeAddedEventProcessingFails() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeAddedEvent event = new LikeAddedEvent(productId, userId);
        
        doThrow(new RuntimeException("DB 오류")).when(productService)
                .updateStatusLikeCount(productId, true);

        // when & then
        likeCountEventListener.handleLikeAdded(event);
        
        verify(productService).updateStatusLikeCount(productId, true);
    }

    @DisplayName("LikeRemovedEvent 처리 중 예외 발생 시에도 오류가 전파되지 않는다")
    @Test
    void doesNotThrowException_whenLikeRemovedEventProcessingFails() {
        // given
        final Long productId = 1L;
        final Long userId = 1L;
        final LikeRemovedEvent event = new LikeRemovedEvent(productId, userId);
        
        doThrow(new RuntimeException("DB 오류")).when(productService)
                .updateStatusLikeCount(productId, false);

        // when & then
        likeCountEventListener.handleLikeRemoved(event);
        
        verify(productService).updateStatusLikeCount(productId, false);
    }
}
