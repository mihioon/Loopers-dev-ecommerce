package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {
    
    @DisplayName("상품을 생성할 때, ")
    @Nested
    class Create {
        
        @DisplayName("상품명이 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenNameIsNull() {
            // given
            final String name = null;
            final String description = "상품 설명";
            final BigDecimal price = BigDecimal.valueOf(10000);
            final String category = "의류";
            final Long brandId = 1L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Product(name, description, price, category, brandId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품명은 필수입니다."));
        }

        @DisplayName("상품명이 공백인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenNameIsEmpty() {
            // given
            final String name = "   ";
            final String description = "상품 설명";
            final BigDecimal price = BigDecimal.valueOf(10000);
            final String category = "의류";
            final Long brandId = 1L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Product(name, description, price, category, brandId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품명은 필수입니다."));
        }

        @DisplayName("가격이 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenPriceIsNull() {
            // given
            final String name = "테스트 상품";
            final String description = "상품 설명";
            final BigDecimal price = null;
            final String category = "의류";
            final Long brandId = 1L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Product(name, description, price, category, brandId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다."));
        }

        @DisplayName("가격이 음수인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenPriceIsNegative() {
            // given
            final String name = "테스트 상품";
            final String description = "상품 설명";
            final BigDecimal price = BigDecimal.valueOf(-1000);
            final String category = "의류";
            final Long brandId = 1L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Product(name, description, price, category, brandId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다."));
        }

        @DisplayName("브랜드 ID가 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenBrandIdIsNull() {
            // given
            final String name = "테스트 상품";
            final String description = "상품 설명";
            final BigDecimal price = BigDecimal.valueOf(10000);
            final String category = "의류";
            final Long brandId = null;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Product(name, description, price, category, brandId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드 ID는 필수입니다."));
        }

        @DisplayName("정상적인 값들로 상품을 생성할 경우, 성공한다.")
        @Test
        void successfullyCreatesProduct_whenAllValuesAreValid() {
            // given
            final String name = "테스트 상품";
            final String description = "상품 설명";
            final BigDecimal price = BigDecimal.valueOf(10000);
            final String category = "의류";
            final Long brandId = 1L;

            // when
            final Product actual = new Product(name, description, price, category, brandId);

            // then
            assertThat(actual.getName()).isEqualTo(name);
            assertThat(actual.getDescription()).isEqualTo(description);
            assertThat(actual.getPrice()).isEqualTo(price);
            assertThat(actual.getCategory()).isEqualTo(category);
            assertThat(actual.getBrandId()).isEqualTo(brandId);
        }
    }

    @DisplayName("상품 이미지를 생성할 때, ")
    @Nested
    class CreateProductImage {
        
        @DisplayName("정상적인 값들로 상품 이미지를 생성할 경우, 성공한다.")
        @Test
        void successfullyCreatesProductImage_whenAllValuesAreValid() {
            // given
            final Long productId = 1L;
            final String imageUrl = "https://example.com/image.jpg";
            final Product.ImageType imageType = Product.ImageType.MAIN;

            // when
            final Product.ProductImage actual = new Product.ProductImage(productId, imageUrl, imageType);

            // then
            assertThat(actual.getImageUrl()).isEqualTo(imageUrl);
            assertThat(actual.getImageType()).isEqualTo(imageType);
        }
    }

    @DisplayName("상품 상세정보를 생성할 때, ")
    @Nested
    class CreateProductDetail {
        
        @DisplayName("정상적인 값들로 상품 상세정보를 생성할 경우, 성공한다.")
        @Test
        void successfullyCreatesProductDetail_whenAllValuesAreValid() {
            // given
            final Long productId = 1L;
            final String description = "description";

            // when
            final Product.ProductDetail actual = new Product.ProductDetail(
                    productId, description
            );

            // then
            assertThat(actual.getDetailContent()).isEqualTo(description);
        }
    }
}
