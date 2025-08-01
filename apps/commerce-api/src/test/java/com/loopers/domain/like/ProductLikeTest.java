package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductLikeTest {

    @DisplayName("상품 좋아요가 정상적으로 생성된다")
    @Test
    void create_Success() {
        // given
        Long productId = 1L;
        Long userId = 2L;

        // when
        ProductLike productLike = new ProductLike(productId, userId);

        // then
        assertThat(productLike.getProductId()).isEqualTo(productId);
        assertThat(productLike.getUserId()).isEqualTo(userId);
    }

    @DisplayName("상품 ID가 null이면 예외가 발생한다")
    @Test
    void create_ProductIdNull() {
        // given
        Long productId = null;
        Long userId = 2L;

        // when & then
        assertThatThrownBy(() -> new ProductLike(productId, userId))
                .isInstanceOf(CoreException.class)
                .hasMessage("상품 ID는 필수입니다.")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("사용자 ID가 null이면 예외가 발생한다")
    @Test
    void create_UserIdNull() {
        // given
        Long productId = 1L;
        Long userId = null;

        // when & then
        assertThatThrownBy(() -> new ProductLike(productId, userId))
                .isInstanceOf(CoreException.class)
                .hasMessage("사용자 ID는 필수입니다.")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("상품 ID와 사용자 ID가 모두 null이면 예외가 발생한다")
    @Test
    void create_BothIdsNull() {
        // given
        Long productId = null;
        Long userId = null;

        // when & then
        assertThatThrownBy(() -> new ProductLike(productId, userId))
                .isInstanceOf(CoreException.class)
                .hasMessage("상품 ID는 필수입니다.")
                .extracting("errorType").isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("같은 사용자가 같은 상품에 좋아요를 중복으로 생성할 수 있다")
    @Test
    void create_DuplicateLike() {
        // given
        Long productId = 1L;
        Long userId = 2L;

        // when
        ProductLike firstLike = new ProductLike(productId, userId);
        ProductLike secondLike = new ProductLike(productId, userId);

        // then
        assertThat(firstLike.getProductId()).isEqualTo(secondLike.getProductId());
        assertThat(firstLike.getUserId()).isEqualTo(secondLike.getUserId());
    }

    @DisplayName("서로 다른 사용자가 같은 상품에 좋아요를 생성할 수 있다")
    @Test
    void create_DifferentUsersLikeSameProduct() {
        // given
        Long productId = 1L;
        Long userId1 = 2L;
        Long userId2 = 3L;

        // when
        ProductLike firstUserLike = new ProductLike(productId, userId1);
        ProductLike secondUserLike = new ProductLike(productId, userId2);

        // then
        assertThat(firstUserLike.getProductId()).isEqualTo(secondUserLike.getProductId());
        assertThat(firstUserLike.getUserId()).isNotEqualTo(secondUserLike.getUserId());
    }

    @DisplayName("같은 사용자가 서로 다른 상품에 좋아요를 생성할 수 있다")
    @Test
    void create_SameUserLikesDifferentProducts() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long userId = 3L;

        // when
        ProductLike firstProductLike = new ProductLike(productId1, userId);
        ProductLike secondProductLike = new ProductLike(productId2, userId);

        // then
        assertThat(firstProductLike.getProductId()).isNotEqualTo(secondProductLike.getProductId());
        assertThat(firstProductLike.getUserId()).isEqualTo(secondProductLike.getUserId());
    }
}