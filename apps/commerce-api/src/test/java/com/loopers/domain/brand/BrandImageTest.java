package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrandImageTest {
    @DisplayName("브랜드 이미지를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("브랜드 ID가 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenBrandIdIsNull() {
            // given
            final Long brandId = null;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new BrandImage(brandId, "https://example.com/logo.png", ImageType.LOGO);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드 ID는 필수입니다."));
        }

        @DisplayName("이미지 타입이 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenImageTypeIsNull() {
            // given
            final ImageType imageType = null;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new BrandImage(1L, "https://example.com/logo.png", imageType);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "이미지 타입은 필수입니다."));
        }

        @DisplayName("정상적인 값으로 생성할 경우, 브랜드 이미지가 생성된다.")
        @Test
        void createsBrandImage_whenValidInput() {
            // given
            final Long brandId = 1L;
            final String imageUrl = "https://example.com/logo.png";
            final ImageType imageType = ImageType.LOGO;

            // when
            final BrandImage actual = new BrandImage(brandId, imageUrl, imageType);

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new BrandImage(brandId, imageUrl, imageType));
        }
    }
}
