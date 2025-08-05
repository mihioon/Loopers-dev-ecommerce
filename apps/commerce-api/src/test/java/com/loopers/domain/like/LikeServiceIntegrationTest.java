package com.loopers.domain.like;

import com.loopers.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

public class LikeServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private LikeService sut;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @DisplayName("상품 좋아요")
    @Nested
    class Like {
        
        @DisplayName("새로운 좋아요 시, 정상적으로 저장된다.")
        @Test
        @Transactional
        void savesLike_whenNewLike() {
            // given
            final Long productId = 1L;
            final Long userId = 1L;
            final LikeCommand.Like command = new LikeCommand.Like(productId, userId);

            // when
            sut.like(command);

            // then
            assertThat(productLikeRepository.isLikedByUser(productId, userId)).isTrue();
            assertThat(sut.getLikeCount(productId)).isEqualTo(1L);
        }

        @DisplayName("이미 좋아요한 상품에 대해 다시 좋아요 시, 멱등성이 보장된다.")
        @Test
        void isIdempotent_whenDuplicateLike() {
            // given
            final Long productId = 1L;
            final Long userId = 1L;
            final LikeCommand.Like command = new LikeCommand.Like(productId, userId);

            sut.like(command);

            // when
            sut.like(command);

            assertThat(productLikeRepository.isLikedByUser(productId, userId)).isTrue();
            assertThat(sut.getLikeCount(productId)).isEqualTo(1L);
        }

        @DisplayName("여러 사용자가 같은 상품에 좋아요 시, 각각 저장된다.")
        @Test
        @Transactional
        void savesMultipleLikes_whenDifferentUsers() {
            // given
            final Long productId = 1L;
            final Long userId1 = 1L;
            final Long userId2 = 2L;

            // when
            sut.like(new LikeCommand.Like(productId, userId1));
            sut.like(new LikeCommand.Like(productId, userId2));

            // then
            assertThat(sut.getLikeCount(productId)).isEqualTo(2L);
            assertThat(productLikeRepository.isLikedByUser(productId, userId1)).isTrue();
            assertThat(productLikeRepository.isLikedByUser(productId, userId2)).isTrue();
        }
    }

    @DisplayName("상품 좋아요 취소")
    @Nested
    class Unlike {
        
        @DisplayName("좋아요한 상품을 취소 시, 정상적으로 삭제된다.")
        @Test
        @Transactional
        void deletesLike_whenUnlike() {
            // given
            final Long productId = 1L;
            final Long userId = 1L;
            sut.like(new LikeCommand.Like(productId, userId));

            // when
            sut.unlike(new LikeCommand.Unlike(productId, userId));

            // then
            assertThat(sut.getLikeCount(productId)).isEqualTo(0L);
            assertThat(productLikeRepository.isLikedByUser(productId, userId)).isFalse();
        }

        @DisplayName("좋아요하지 않은 상품을 취소 시, 멱등성이 보장된다.")
        @Test
        @Transactional
        void isIdempotent_whenUnlikeNonExistent() {
            // given
            final Long productId = 1L;
            final Long userId = 1L;
            final LikeCommand.Unlike command = new LikeCommand.Unlike(productId, userId);

            // when
            sut.unlike(command);

            // then
            assertThat(sut.getLikeCount(productId)).isEqualTo(0L);
            assertThat(productLikeRepository.isLikedByUser(productId, userId)).isFalse();
        }
    }

    @DisplayName("좋아요 수 조회")
    @Nested
    class GetLikeCount {
        
        @DisplayName("좋아요가 없는 상품의 좋아요 수는 0이다.")
        @Test
        @Transactional
        void returnsZero_whenNoLikes() {
            // given
            final Long productId = 999L;

            // when
            final Long likeCount = sut.getLikeCount(productId);

            // then
            assertThat(likeCount).isEqualTo(0L);
        }

        @DisplayName("여러 사용자가 좋아요한 상품의 좋아요 수를 정확히 반환한다.")
        @Test
        @Transactional
        void returnsCorrectCount_whenMultipleLikes() {
            // given
            final Long productId = 1L;
            
            // 3명의 사용자가 좋아요
            sut.like(new LikeCommand.Like(productId, 1L));
            sut.like(new LikeCommand.Like(productId, 2L));
            sut.like(new LikeCommand.Like(productId, 3L));

            // when
            final Long likeCount = sut.getLikeCount(productId);

            // then
            assertThat(likeCount).isEqualTo(3L);
        }
    }

    @DisplayName("사용자 좋아요 상태 확인")
    @Nested
    class IsLikedByUser {
        
        @DisplayName("좋아요한 사용자는 true를 반환한다.")
        @Test
        @Transactional
        void returnsTrue_whenUserLiked() {
            // given
            final Long productId = 1L;
            final Long userId = 1L;
            
            sut.like(new LikeCommand.Like(productId, userId));

            // when
            final Boolean isLiked = sut.isLikedByUser(productId, userId);

            // then
            assertThat(isLiked).isTrue();
        }

        @DisplayName("좋아요하지 않은 사용자는 false를 반환한다.")
        @Test
        @Transactional
        void returnsFalse_whenUserNotLiked() {
            // given
            final Long productId = 1L;
            final Long userId = 1L;

            // when
            final Boolean isLiked = sut.isLikedByUser(productId, userId);

            // then
            assertThat(isLiked).isFalse();
        }
    }

    @DisplayName("사용자 좋아요 상품 목록 조회")
    @Nested
    class GetUserLikeProducts {
        
        @DisplayName("좋아요한 상품이 없는 사용자는 빈 목록을 반환한다.")
        @Test
        @Transactional
        void returnsEmptyList_whenUserHasNoLikes() {
            // given
            final Long userId = 1L;

            // when
            final UserLikeProductInfo result = sut.getUserLikeProducts(userId);

            // then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(new UserLikeProductInfo(List.of()));
        }

        @DisplayName("사용자가 좋아요한 상품 목록을 반환한다.")
        @Test
        @Transactional
        void returnsLikedProducts_whenUserHasLikes() {
            // given
            final Long userId = 1L;
            final Long productId1 = 1L;
            final Long productId2 = 2L;
            final Long productId3 = 3L;
            
            sut.like(new LikeCommand.Like(productId1, userId));
            sut.like(new LikeCommand.Like(productId3, userId));

            // when
            final UserLikeProductInfo actual = sut.getUserLikeProducts(userId);

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new UserLikeProductInfo(List.of(productId1, productId3)));
        }
    }
}
