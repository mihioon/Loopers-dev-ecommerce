package com.loopers.domain.like;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private LikeTransactionHelper likeTransactionHelper;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("좋아요 등록 시 likeTransactionHelper의 saveLike가 호출된다")
    @Test
    void callsRepositorySave_whenLike() {
        // given
        final LikeCommand.Like command = new LikeCommand.Like(1L, 1L);

        // when
        likeService.like(command);

        // then
        verify(likeTransactionHelper).saveLike(command);
    }

    @DisplayName("좋아요 등록 과정에서 DataIntegrityViolationException 발생 시, 예외를 무시한다")
    @Test
    void ignoresException_whenDuplicateLike() {
        // given
        final LikeCommand.Like command = new LikeCommand.Like(1L, 1L);
        doThrow(new DataIntegrityViolationException("중복"))
                .when(likeTransactionHelper).saveLike(any());

        // when & then
        assertDoesNotThrow(() -> likeService.like(command));
    }

    @DisplayName("좋아요 취소 시 productLikeRepository의 deleteByProductIdAndUserId가 호출된다")
    @Test
    void callsRepositoryDelete_whenUnlike() {
        // given
        final LikeCommand.Unlike command = new LikeCommand.Unlike(1L, 1L);

        // when
        likeService.unlike(command);

        // then
        verify(productLikeRepository).deleteByProductIdAndUserId(command.productId(), command.userId());
    }

    @DisplayName("중복 좋아요 취소 시 아무 일도 하지 않고 예외를 발생시키지 않는다.")
    @Test
    void ignoresException_whenDuplicateUnlike() {
        // given
        final LikeCommand.Unlike command = new LikeCommand.Unlike(1L, 1L);

        likeService.unlike(command);

        // when & then
        assertDoesNotThrow(() -> likeService.unlike(command));
    }
}
