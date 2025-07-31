package com.loopers.domain.brand;

import com.loopers.domain.catalog.brand.*;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class BrandServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private BrandService sut;

    @Autowired
    private BrandRepository brandRepository;

    @DisplayName("브랜드 생성")
    @Nested
    class Create {
        @DisplayName("새로운 브랜드명으로 생성 시도 시, 성공한다.")
        @Test
        void saveBrand_whenCreateWithNewName() {
            // given
            final String brandName = "테스트브랜드";
            List<BrandCommand.BrandImageCommand.Create> images = List.of(
                    new BrandCommand.BrandImageCommand.Create(null, "https://example.com/logo.png", Brand.ImageType.LOGO),
                    new BrandCommand.BrandImageCommand.Create(null, "https://example.com/banner.png", Brand.ImageType.BANNER)
            );
            BrandCommand.Create command = new BrandCommand.Create(brandName, "테스트 브랜드 설명", images);

            // when
            BrandInfo actual = sut.create(command);

            // then
            assertThat(actual.name()).isEqualTo(brandName);
            assertThat(actual.description()).isEqualTo("테스트 브랜드 설명");
            assertThat(actual.images()).hasSize(2);
            assertThat(actual.images()).extracting("imageType")
                    .containsExactly(Brand.ImageType.LOGO, Brand.ImageType.BANNER);
        }

        @DisplayName("이미지 없이 브랜드만 생성할 경우, 성공한다.")
        @Test
        void saveBrand_whenCreateWithoutImages() {
            // given
            final String brandName = "이미지없는브랜드";
            BrandCommand.Create command = new BrandCommand.Create(brandName, "설명", List.of());

            // when
            BrandInfo actual = sut.create(command);

            // then
            assertThat(actual.name()).isEqualTo(brandName);
            assertThat(actual.images()).isEmpty();
        }

        @DisplayName("이미 존재하는 브랜드명으로 생성 시도 시, CONFLICT 예외가 발생한다.")
        @Test
        void fail_whenCreateWithDuplicateName() {
            // given
            final String brandName = "중복브랜드";
            BrandCommand.Create command = new BrandCommand.Create(brandName, "테스트 브랜드 설명", List.of());
            sut.create(command);

            // when
            final CoreException actual = assertThrows(CoreException.class, () ->
                    sut.create(command));

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 존재하는 브랜드명입니다."));
        }
    }

    @DisplayName("브랜드 조회")
    @Nested
    class Read {
        @DisplayName("해당 ID의 브랜드가 존재할 경우, 브랜드 정보가 반환된다.")
        @Test
        void returnsBrandInformation_whenBrandExists() {
            // given
            Brand brand = brandRepository.save(new Brand("brand", "description"));
            List<BrandInfo.BrandImageInfo> images = List.of();

            // when
            BrandInfo actual = sut.get(brand.getId());

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isNotNull()
                    .isEqualTo(BrandInfo.from(brand, images));
        }

        @DisplayName("해당 ID의 브랜드가 존재하지 않을 경우, NOT_FOUND 예외가 반환된다.")
        @Test
        void returnsNotFoundException_whenBrandDoesNotExist() {
            // given
            final Long brandId = 1L;

            // when
            CoreException actual = assertThrows(CoreException.class, () -> {
                sut.get(brandId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));
        }
    }
}
