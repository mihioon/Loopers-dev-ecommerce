package com.loopers.application.like;

import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.user.*;
import com.loopers.support.IntegrationTest;
import com.loopers.support.TestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Like 파사드 동시성 테스트")
public class LikeFacadeIntegrationTest extends IntegrationTest {
    @Autowired
    private LikeFacade sut;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestHelper testHelper;

    @DisplayName("상품 좋아요")
    @Nested
    class Like {

        @DisplayName("새로운 좋아요 시, 정상적으로 저장된다.")
        @Test
        void savesLike_whenNewLike() {
            // given
            testHelper.prepareLikeCount(1L);
            User user = userRepository.save(new User(new LoginId("test123456"), new Email("test@example.com"), new BirthDate("2025-01-01"), Gender.F, "test"));
            final Long productId = 1L;
            final String loginId = "test123456";

            // when
            sut.like(productId, loginId);

            // then
            assertThat(productLikeRepository.isLikedByUser(productId, user.getId())).isTrue();
            assertThat(sut.getLikeCount(productId)).isEqualTo(1L);
        }

        @DisplayName("이미 좋아요한 상품에 대해 다시 좋아요 시, 멱등성이 보장된다.")
        @Test
        void isIdempotent_whenDuplicateLike() {
            // given
            testHelper.prepareLikeCount(1L);
            User user = userRepository.save(new User(new LoginId("test123456"), new Email("test@example.com"), new BirthDate("2025-01-01"), Gender.F, "test"));
            final Long productId = 1L;
            final String loginId = "test123456";

            sut.like(productId, loginId);

            // when
            sut.like(productId, loginId);

            assertThat(productLikeRepository.isLikedByUser(productId, user.getId())).isTrue();
            assertThat(sut.getLikeCount(productId)).isEqualTo(1L);
        }

        @DisplayName("여러 사용자가 같은 상품에 좋아요 시, 각각 저장된다.")
        @Test
        void savesMultipleLikes_whenDifferentUsers() {
            // given
            testHelper.prepareLikeCount(1L);
            final Long productId = 1L;

            final String loginId = "test123456";
            User user = userRepository.save(new User(new LoginId(loginId), new Email("test@example.com"), new BirthDate("2025-01-01"), Gender.F, "test"));
            final String loginId2 = "test123457";
            User user2 = userRepository.save(new User(new LoginId(loginId2), new Email("test2@example.com"), new BirthDate("2025-01-01"), Gender.F, "test2"));

            // when
            sut.like(productId, loginId);
            sut.like(productId, loginId2);

            // then
            assertThat(sut.getLikeCount(productId)).isEqualTo(2L);
            assertThat(productLikeRepository.isLikedByUser(productId, user.getId())).isTrue();
            assertThat(productLikeRepository.isLikedByUser(productId, user2.getId())).isTrue();
        }
    }

    @DisplayName("상품 좋아요 취소")
    @Nested
    class Unlike {

        @DisplayName("좋아요한 상품을 취소 시, 정상적으로 삭제된다.")
        @Test
        void deletesLike_whenUnlike() {
            // given
            testHelper.prepareLikeCount(1L);
            User user = userRepository.save(new User(new LoginId("test123456"), new Email("test@example.com"), new BirthDate("2025-01-01"), Gender.F, "test"));
            final Long productId = 1L;
            final String loginId = "test123456";
            sut.like(productId, loginId);

            // when
            sut.unlike(productId, loginId);

            // then
            assertThat(sut.getLikeCount(productId)).isEqualTo(0L);
            assertThat(productLikeRepository.isLikedByUser(productId, user.getId())).isFalse();
        }

        @DisplayName("좋아요하지 않은 상품을 취소 시, 멱등성이 보장된다.")
        @Test
        void isIdempotent_whenUnlikeNonExistent() {
            // given
            testHelper.prepareLikeCount(1L);
            User user = userRepository.save(new User(new LoginId("test123456"), new Email("test@example.com"), new BirthDate("2025-01-01"), Gender.F, "test"));
            final Long productId = 1L;
            final String loginId = "test123456";

            // when
            sut.unlike(productId, loginId);

            // then
            assertThat(sut.getLikeCount(productId)).isEqualTo(0L);
            assertThat(productLikeRepository.isLikedByUser(productId, user.getId())).isFalse();
        }
    }

    @DisplayName("좋아요 수 조회")
    @Nested
    class GetLikeCount {

        @DisplayName("좋아요가 없는 상품의 좋아요 수는 0이다.")
        @Test
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
        void returnsCorrectCount_whenMultipleLikes() {
            // given
            testHelper.prepareLikeCount(1L, 2L, 3L);

            String loginId = "test123456";
            String loginId2 = "test123457";
            String loginId3 = "test123458";
            User user = userRepository.save(new User(new LoginId(loginId), new Email("test@example.com"), new BirthDate("2025-01-01"), Gender.F, "test"));
            User user2 = userRepository.save(new User(new LoginId(loginId2), new Email("test2@example.com"), new BirthDate("2025-01-01"), Gender.F, "test2"));
            User user3 = userRepository.save(new User(new LoginId(loginId3), new Email("test3@example.com"), new BirthDate("2025-01-01"), Gender.F, "test3"));

            final Long productId = 1L;


            // 3명의 사용자가 좋아요
            sut.like(productId, loginId);
            sut.like(productId, loginId2);
            sut.like(productId, loginId3);

            // when
            final Long likeCount = sut.getLikeCount(productId);

            // then
            assertThat(likeCount).isEqualTo(3L);
        }
    }
}
