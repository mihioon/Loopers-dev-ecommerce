package com.loopers.domain.brand;

import com.loopers.domain.catalog.brand.Brand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrandTest {
    @DisplayName("브랜드를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("브랜드명이 빈 값인 경우, BAD_REQUEST 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void throwsBadRequestException_whenNameIsNull(String name) {
            // given

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                new Brand(name, "description");
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드명은 필수입니다."));
        }
    }
}
