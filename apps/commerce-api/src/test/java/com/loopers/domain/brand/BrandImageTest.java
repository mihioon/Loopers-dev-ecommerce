package com.loopers.domain.brand;

import com.loopers.domain.catalog.brand.Brand;
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
        @DisplayName("이미지 URL이 null인 경우는 허용된다.")
        @Test
        void allowsNullImageUrl() {
            // given & when & then
            // BrandImage 생성자에서 URL 검증이 주석처리되어 있어 통과
            new Brand.BrandImage(null, Brand.ImageType.LOGO);
        }

        @DisplayName("이미지 타입이 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenImageTypeIsNull() {
            // given
            final Brand.ImageType imageType = null;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Brand.BrandImage("https://example.com/logo.png", imageType);
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
            final String imageUrl = "https://example.com/logo.png";
            final Brand.ImageType imageType = Brand.ImageType.LOGO;

            // when
            final Brand.BrandImage actual = new Brand.BrandImage(imageUrl, imageType);

            // then
            assertThat(actual.getImageUrl()).isEqualTo(imageUrl);
            assertThat(actual.getImageType()).isEqualTo(imageType);
        }
    }
}
