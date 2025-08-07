package com.loopers.domain.like;

import com.loopers.support.TestHelper;
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

    @Autowired
    private TestHelper testHelper;

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
        void returnsLikedProducts_whenUserHasLikes() {
            // given
            testHelper.prepareLikeCount(1L, 2L, 3L);
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
