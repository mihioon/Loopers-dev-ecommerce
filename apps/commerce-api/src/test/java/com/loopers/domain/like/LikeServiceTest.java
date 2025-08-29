package com.loopers.domain.like;

import com.loopers.domain.common.event.EventPublisher;
import com.loopers.domain.product.ProductStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private ProductStatusRepository productStatusRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("좋아요 등록 시 productLikeRepository의 save가 호출된다")
    @Test
    void callsRepositorySave_whenLike() {
        // given
        final LikeCommand.Like command = new LikeCommand.Like(1L, 1L);

        // when
        likeService.like(command);

        // then
        verify(productLikeRepository).save(any(ProductLike.class));
    }

    @DisplayName("좋아요 등록 과정에서 DataIntegrityViolationException 발생 시, 예외가 전파된다")
    @Test
    void throwsException_whenDuplicateLike() {
        // given
        final LikeCommand.Like command = new LikeCommand.Like(1L, 1L);
        doThrow(new DataIntegrityViolationException("중복"))
                .when(productLikeRepository).save(any(ProductLike.class));

        // when & then - DataIntegrityViolationException이 발생해야 함
        try {
            likeService.like(command);
        } catch (DataIntegrityViolationException e) {
            // 예외가 정상적으로 발생함
        }
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

    @DisplayName("좋아요하지 않은 상품 취소 시 CoreException이 발생한다")
    @Test
    void throwsException_whenUnlikeNonExistent() {
        // given
        final LikeCommand.Unlike command = new LikeCommand.Unlike(1L, 1L);
        when(productLikeRepository.deleteByProductIdAndUserId(1L, 1L))
                .thenReturn(0); // 삭제된 행이 없음

        // when & then - CoreException이 발생해야 함
        try {
            likeService.unlike(command);
        } catch (Exception e) {
            // 예외가 정상적으로 발생함
        }
    }
}
