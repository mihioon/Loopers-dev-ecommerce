package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ProductBrandService sut;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private BrandRepository brandRepository;

    @DisplayName("상품 상세 조회")
    @Nested
    class GetDetail {
        
        @DisplayName("존재하는 상품 ID로 상세 조회 시, 상품 정보가 반환된다.")
        @Test
        @Transactional
        void getDetail_whenProductExists() {
            // given
            Brand brand = brandRepository.save(new Brand("Test Brand", "Test Description"));
            Product product = saveTestProductWithBrand(brand.getId());

            // when
            final ProductInfo.Detail actual = sut.getDetail(product.getId());

            // then
            assertThat(actual.id()).isEqualTo(product.getId());
            assertThat(actual.name()).isEqualTo(product.getName());
            assertThat(actual.brandId()).isEqualTo(product.getBrandId());
            assertThat(actual.brandInfo()).isNotNull();
            assertThat(actual.brandInfo().name()).isEqualTo("Test Brand");
        }

        @DisplayName("존재하지 않는 상품 ID로 조회 시, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsNotFoundException_whenProductDoesNotExist() {
            // given
            final Long nonExistentProductId = 999L;

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                sut.getDetail(nonExistentProductId);
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        }

        @DisplayName("상품은 존재하지만 상세정보가 없는 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        @Transactional
        void throwsNotFoundException_whenProductDetailDoesNotExist() {
            // given
            Brand brand = brandRepository.save(new Brand("Test Brand", "Test Description"));
            final Product product = productRepository.save(new Product("name", "description", BigDecimal.valueOf(10000), "category", brand.getId()));

            // when
            final CoreException actual = assertThrows(CoreException.class, () -> {
                sut.getDetail(product.getId());
            });

            // then
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "상품 상세 정보를 찾을 수 없습니다."));
        }
    }

    @DisplayName("상품 목록 조회")
    @Nested
    class GetSummery {
        
        @DisplayName("정렬 조건으로 상품 목록 조회 시, 정렬된 상품 목록이 반환된다.")
        @ParameterizedTest
        @ValueSource(strings = {"latest", "price_asc"})
        void getSummery_whenSortCommand(String sortType) {
            // given
            productRepository.save(new Product(
                    "상품 A", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            productRepository.save(new Product(
                    "상품 B", "설명", BigDecimal.valueOf(20000), "전자제품", 2L
            ));
            productRepository.save(new Product(
                    "상품 C", "설명", BigDecimal.valueOf(30000), "의류", 3L
            ));

            // when
            final ProductInfo.Summery actual = sut.getSummery(new ProductQuery.Summery(
                    null,
                    null,
                    ProductQuery.Summery.SortType.from(sortType),
                    0,
                    10
            ));

            // then
            if(sortType.equals("latest")) {
                assertThat(actual.products()).extracting(ProductInfo.Summery.Item::name)
                        .containsSequence("상품 C", "상품 B", "상품 A");
            } else if (sortType.equals("price_asc")) {
                assertThat(actual.products()).extracting(ProductInfo.Summery.Item::name)
                        .containsSequence("상품 A", "상품 B", "상품 C");
            }
        }

        @DisplayName("브랜드 필터로 상품 목록 조회 시, 해당 브랜드의 상품만 반환된다.")
        @Test
        void getSummery_whenBrandFilter() {
            // given
            final Long targetBrandId = 5L;
            productRepository.save(new Product(
                    "브랜드 5 상품 1", "설명", BigDecimal.valueOf(10000), "의류", targetBrandId
            ));
            productRepository.save(new Product(
                    "브랜드 5 상품 2", "설명", BigDecimal.valueOf(20000), "전자제품", targetBrandId
            ));
            productRepository.save(new Product(
                    "다른 브랜드 상품", "설명", BigDecimal.valueOf(30000), "의류", 999L
            ));

            final ProductQuery.Summery command = new ProductQuery.Summery(
                    null,
                    targetBrandId,
                    ProductQuery.Summery.SortType.LATEST,
                    0,
                    10
            );

            // when
            final ProductInfo.Summery actual = sut.getSummery(command);

            // then
            assertThat(actual.products()).hasSizeGreaterThanOrEqualTo(2);
            assertThat(actual.products()).extracting(ProductInfo.Summery.Item::brandId)
                    .allMatch(brandId -> targetBrandId.equals(brandId));
        }
    }

    private Product saveTestProduct() {
        final Product product = new Product("name", "description", BigDecimal.valueOf(10000), "category", 1L);
        for (int i = 0; i < 4; i++) {
            product.addImage(new Product.ProductImage(null, "url" + i, Product.ImageType.values()[i]));
        }
        product.setDetail(new Product.ProductDetail(null, "description"));
        return productRepository.save(product);
    }
    
    private Product saveTestProductWithBrand(Long brandId) {
        final Product product = new Product("name", "description", BigDecimal.valueOf(10000), "category", brandId);
        for (int i = 0; i < 4; i++) {
            product.addImage(new Product.ProductImage(null, "url" + i, Product.ImageType.values()[i]));
        }
        product.setDetail(new Product.ProductDetail(null, "description"));
        return productRepository.save(product);
    }
}
