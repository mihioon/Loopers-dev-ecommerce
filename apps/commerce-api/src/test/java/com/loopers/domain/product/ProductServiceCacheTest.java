package com.loopers.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductServiceCacheTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductStatusRepository productStatusRepository;
    
    @Mock
    private ProductCacheRepository productCacheRepository;
    
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("캐시가 없는 경우, DB에서 상품 개수를 조회하고 캐시에 저장한다")
    void countProductsWithFilter_NoCacheHit_ShouldQueryDbAndCache() {
        // given
        String category = "상의";
        Long brandId = 1L;
        String expectedCacheKey = productService.createCacheKey(category, brandId);
        long dbCount = 100L;
        
        given(productCacheRepository.get(expectedCacheKey)).willReturn(null);
        given(productRepository.countProductsWithFilter(category, brandId)).willReturn(dbCount);
        
        // when
        long result = productService.countProductsWithFilter(category, brandId);
        
        // then
        assertThat(result).isEqualTo(dbCount);
        then(productCacheRepository).should().get(expectedCacheKey); // 캐시 조회 1회
        then(productRepository).should().countProductsWithFilter(category, brandId); // DB 조회 1회
        then(productCacheRepository).should().set(expectedCacheKey, dbCount, Duration.ofHours(1)); // 캐시 저장 1회
    }

    @Test
    @DisplayName("캐시가 있는 경우, DB 조회 없이 캐시에서 상품 개수를 반환한다")
    void countProductsWithFilter_CacheHit_ShouldReturnCachedValue() {
        // given
        String category = "하의";
        Long brandId = 2L;
        String expectedCacheKey = productService.createCacheKey(category, brandId);
        long cachedCount = 50L;
        
        given(productCacheRepository.get(expectedCacheKey)).willReturn(cachedCount);
        
        // when
        long result = productService.countProductsWithFilter(category, brandId);
        
        // then
        assertThat(result).isEqualTo(cachedCount);
        then(productCacheRepository).should().get(expectedCacheKey); // 캐시 조회 1회
        then(productRepository).should(never()).countProductsWithFilter(any(), any()); // DB 조회 0회
        then(productCacheRepository).should(never()).set(any(), any(), any()); // 캐시 저장 0회
    }

    @Test
    @DisplayName("같은 필터 조건(브랜드, 카테고리)으로 여러 번 호출하는 경우, 첫 번째 호출 후에는 캐시를 사용한다")
    void countProductsWithFilter_MultipleCalls_ShouldUseCacheAfterFirstCall() {
        // given
        String category = "상의";
        Long brandId = 3L;
        String expectedCacheKey = productService.createCacheKey(category, brandId);
        long count = 75L;
        
        given(productCacheRepository.get(expectedCacheKey))
                .willReturn(null)
                .willReturn(count);
        given(productRepository.countProductsWithFilter(category, brandId)).willReturn(count);
        
        // when
        long firstResult = productService.countProductsWithFilter(category, brandId);
        long secondResult = productService.countProductsWithFilter(category, brandId);
        
        // then
        assertThat(firstResult).isEqualTo(count);
        assertThat(secondResult).isEqualTo(count);
        then(productCacheRepository).should(times(2)).get(expectedCacheKey); // 캐시 조회 2회
        then(productRepository).should(times(1)).countProductsWithFilter(category, brandId); // DB 조회 1회
        then(productCacheRepository).should(times(1)).set(expectedCacheKey, count, Duration.ofHours(1)); // 캐시 저장 1회
    }

    @ParameterizedTest(name = "{index} => 카테고리={0}, 브랜드ID={1}, 캐시 키={2}")
    @MethodSource("provideCreateCacheKeyCases")
    @DisplayName("일부 필터가 없는 경우, 캐시 키가 올바르게 생성된다")
    void createCacheKey_shouldGenerateCorrectKey_withDifferentCases(
            String category, Long brandId, String expectedCacheKey
    ) {
        // when
        String actualCacheKey = productService.createCacheKey(category, brandId);

        // then
        assertThat(actualCacheKey).isEqualTo(expectedCacheKey);
    }

    private static Stream<Arguments> provideCreateCacheKeyCases() {
        return Stream.of(
                Arguments.of("상의", 1L, "product_count:category=상의:brand=1"),
                Arguments.of(null, 1L, "product_count:category=null:brand=1"),
                Arguments.of("하의", null, "product_count:category=하의:brand=null"),
                Arguments.of(null, null, "product_count:category=null:brand=null") // 전체 상품 카운트
        );
    }
}
