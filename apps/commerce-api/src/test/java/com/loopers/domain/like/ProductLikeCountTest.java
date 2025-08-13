package com.loopers.domain.like;

import com.loopers.domain.product.ProductStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ProductStatus 엔티티 테스트")
class ProductStatusTest {

    @DisplayName("ProductStatus 생성")
    @Nested
    class CreateProductStatus {

        @DisplayName("유효한 productId로 생성 시 초기 카운트는 0이다")
        @Test
        void create_withValidProductId_shouldInitializeWithZeroCount() {
            // given
            Long productId = 1L;

            // when
            ProductStatus likeCount = new ProductStatus(productId);

            // then
            assertThat(likeCount.getProductId()).isEqualTo(productId);
            assertThat(likeCount.getLikeCount()).isEqualTo(0);
        }

        @DisplayName("productId가 null일 때 BAD_REQUEST 예외가 발생한다")
        @Test
        void create_withNullProductId_shouldThrowException() {
            // when
            CoreException actual = assertThrows(CoreException.class, () -> {
                new ProductStatus(null);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다."));
        }
    }

    @DisplayName("좋아요 카운트 증가")
    @Nested
    class IncrementLikeCount {

        @DisplayName("카운트 증가 시 1씩 증가한다")
        @Test
        void incrementLikeCount_shouldIncreaseByOne() {
            // given
            ProductStatus likeCount = new ProductStatus(1L);

            // when
            likeCount.increase();

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(1);
        }

        @DisplayName("여러 번 증가 시 올바르게 누적된다")
        @Test
        void incrementLikeCount_multiple_shouldAccumulateCorrectly() {
            // given
            ProductStatus likeCount = new ProductStatus(1L);

            // when
            for (int i = 0; i < 5; i++) {
                likeCount.increase();
            }

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(5);
        }
    }

    @DisplayName("좋아요 카운트 감소")
    @Nested
    class DecrementLikeCount {

        @DisplayName("카운트가 1 이상일 때 감소 시 1씩 감소한다")
        @Test
        void decrementLikeCount_whenCountIsPositive_shouldDecreaseByOne() {
            // given
            ProductStatus likeCount = new ProductStatus(1L);
            likeCount.increase(); //+1

            // when
            likeCount.decrease(); //-1

            // then
            assertThat(likeCount.getLikeCount()).isEqualTo(0);
        }

        @DisplayName("카운트가 0일 때 감소 시 BAD_REQUEST 예외가 발생한다")
        @Test
        void decrementLikeCount_whenCountIsZero_shouldThrowException() {
            // given
            ProductStatus likeCount = new ProductStatus(1L);

            // when
            CoreException actual = assertThrows(CoreException.class, likeCount::decrease);

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "좋아요 수는 0보다 작을 수 없습니다."));
        }
    }
}
