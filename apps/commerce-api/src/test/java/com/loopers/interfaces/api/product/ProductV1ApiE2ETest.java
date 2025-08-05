package com.loopers.interfaces.api.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.user.*;
import com.loopers.support.E2EIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductV1ApiE2ETest extends E2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BrandRepository brandRepository;

    @DisplayName("GET /api/v1/products")
    @Nested
    class GetProducts {
        private static final String ENDPOINT = "/api/v1/products/";

        @DisplayName("상품 목록 조회가 성공할 경우, 상품 목록을 응답으로 반환한다.")
        @Test
        @Transactional
        void returnsProductList_whenGetProductsSuccessful() throws Exception {
            // given
            final Product product1 = productRepository.save(new Product(
                    "테스트 상품 1", "설명1", BigDecimal.valueOf(10000), "의류", 1L
            ));
            final Product product2 = productRepository.save(new Product(
                    "테스트 상품 2", "설명2", BigDecimal.valueOf(20000), "전자제품", 2L
            ));

            // 좋아요 데이터 생성
            productLikeRepository.save(new ProductLike(product1.getId(), 1L));
            productLikeRepository.save(new ProductLike(product1.getId(), 2L));
            productLikeRepository.save(new ProductLike(product2.getId(), 1L));

            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortType", "latest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products").isArray())
                    .andExpect(jsonPath("$.data.currentPage").value(0))
                    .andExpect(jsonPath("$.data.totalElements").exists());
        }

        @DisplayName("카테고리 필터링으로 상품 목록 조회 시, 해당 카테고리의 상품만 반환한다.")
        @Test
        @Transactional
        void returnsFilteredProductList_whenCategoryFilterProvided() throws Exception {
            // given
            final Product clothingProduct = productRepository.save(new Product(
                    "의류 상품", "의류 설명", BigDecimal.valueOf(15000), "의류", 1L
            ));
            final Product electronicsProduct = productRepository.save(new Product(
                    "전자제품", "전자제품 설명", BigDecimal.valueOf(25000), "전자제품", 2L
            ));

            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("category", "의류")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products").isArray());
        }

        @DisplayName("사용자 로그인 상태에서 상품 목록 조회 시, 좋아요 상태가 포함된다.")
        @Test
        @Transactional
        void returnsProductListWithLikeStatus_whenUserIsLoggedIn() throws Exception {
            // given
            final Product product = productRepository.save(new Product(
                    "좋아요 테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            
            final String loginId = "test123456";
            productLikeRepository.save(new ProductLike(product.getId(), 1L));

            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products").isArray());
        }

        @DisplayName("정렬 조건으로 가격 오름차순 조회 시, 가격순으로 정렬된 결과가 반환된다.")
        @Test
        @Transactional
        void returnsProductsSortedByPrice_whenPriceAscSortProvided() throws Exception {
            // given
            productRepository.save(new Product(
                    "비싼 상품", "설명", BigDecimal.valueOf(30000), "의류", 1L
            ));
            productRepository.save(new Product(
                    "저렴한 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));

            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("sortType", "price_asc")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products").isArray());
        }
    }

    @DisplayName("GET /api/v1/products/{productId}")
    @Nested
    class GetProduct {
        private static final String ENDPOINT = "/api/v1/products/{productId}";

        @DisplayName("상품 상세 조회가 성공할 경우, 상품 상세 정보를 응답으로 반환한다.")
        @Test
        @Transactional
        void returnsProductDetail_whenGetProductSuccessful() throws Exception {
            // given
            // 브랜드 데이터 생성
            Brand brand = brandRepository.save(new Brand("테스트 브랜드", "test@brand.com"));
            
            final Product product = new Product(
                    "상세 조회 테스트 상품",
                    "상품 상세 설명",
                    BigDecimal.valueOf(15000),
                    "의류",
                    1L
            );

            product.addImage(new Product.ProductImage(
                    null, "https://example.com/test-image.jpg", Product.ImageType.MAIN
            ));

            product.setDetail(new Product.ProductDetail(
                    null, "테스트 상품 상세 설명"
            ));
            
            Product savedProduct = productRepository.save(product);

            // 좋아요 데이터
            productLikeRepository.save(new ProductLike(savedProduct.getId(), 1L));
            productLikeRepository.save(new ProductLike(savedProduct.getId(), 2L));

            // when&then
            mockMvc.perform(get(ENDPOINT, savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.id").value(savedProduct.getId()))
                    .andExpect(jsonPath("$.data.name").value("상세 조회 테스트 상품"))
                    .andExpect(jsonPath("$.data.description").value("상품 상세 설명"))
                    .andExpect(jsonPath("$.data.price").value(15000))
                    .andExpect(jsonPath("$.data.category").value("의류"))
                    .andExpect(jsonPath("$.data.brandId").value(1))
                    .andExpect(jsonPath("$.data.likeCount").value(2))
                    .andExpect(jsonPath("$.data.imageUrl").value("https://example.com/test-image.jpg"));
        }

        @DisplayName("로그인한 사용자가 상품 상세 조회 시, 좋아요 상태가 포함된다.")
        @Test
        @Transactional
        void returnsProductDetailWithLikeStatus_whenUserIsLoggedIn() throws Exception {
            // given
            // 브랜드 데이터 생성
            Brand brand = brandRepository.save(new Brand("테스트 브랜드", "test@brand.com"));
            
            final Product product = new Product(
                    "상세 조회 테스트 상품",
                    "상품 상세 설명",
                    BigDecimal.valueOf(15000),
                    "의류",
                    1L
            );

            product.addImage(new Product.ProductImage(
                    null, "https://example.com/test-image.jpg", Product.ImageType.MAIN
            ));

            product.setDetail(new Product.ProductDetail(
                    null, "테스트 상품 상세 설명"
            ));

            Product savedProduct = productRepository.save(product);

            final String loginId = "test123456";
            userRepository.save(new User(
                    new LoginId(loginId),
                    new Email("test@example.com"),
                    new BirthDate("2025-01-01"),
                    Gender.F,
                    "test"
            ));
            productLikeRepository.save(new ProductLike(savedProduct.getId(), 1L));

            // when&then
            mockMvc.perform(get(ENDPOINT, savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.id").value(savedProduct.getId()))
                    .andExpect(jsonPath("$.data.likeCount").value(1));
        }

        @DisplayName("존재하지 않는 상품 ID로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void returnsNotFound_whenProductDoesNotExist() throws Exception {
            // given
            final Long nonExistentProductId = 999L;

            // when&then
            mockMvc.perform(get(ENDPOINT, nonExistentProductId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"));
        }
    }
}
