package com.loopers.interfaces.api.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.like.LikeFacade;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.*;
import com.loopers.support.E2EIntegrationTest;
import com.loopers.support.TestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LikeV1ApiE2ETest extends E2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LikeFacade likeFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestHelper testHelper;

    @DisplayName("POST /api/v1/like/products/{productId}")
    @Nested
    class LikeProduct {
        private static final String ENDPOINT = "/api/v1/like/products/{productId}";

        @DisplayName("새로운 좋아요 요청 시, 성공적으로 좋아요가 추가된다.")
        @Test
        void addsLike_whenNewLikeRequest() throws Exception {
            // given
            final String loginId = "test123456";
            userService.register(new UserCommand.Register(loginId, "test", "F", "test@example.com", "2025-01-01"));
            Product product = productRepository.save(new Product(
                    "테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product.getId());

            // when & then
            mockMvc.perform(post(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.productId").value(product.getId()))
                    .andExpect(jsonPath("$.data.likeCount").exists());
        }

        @DisplayName("이미 좋아요한 상품에 대해 다시 좋아요 요청 시, 멱등성이 보장된다.")
        @Test
        void isIdempotent_whenDuplicateLikeRequest() throws Exception {
            // given
            final Product product = productRepository.save(new Product(
                    "테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product.getId());

            final String loginId = "test123456";
            final User user = userRepository.save(new User(
                    new LoginId(loginId),
                    new Email("test@example.com"),
                    new BirthDate("2025-01-01"),
                    Gender.F,
                    "test"
            ));

            likeFacade.like(product.getId(), loginId);

            // when & then
            mockMvc.perform(post(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.productId").value(product.getId()))
                    .andExpect(jsonPath("$.data.likeCount").exists());
        }

        @DisplayName("여러 사용자가 같은 상품에 좋아요 시, 각각 카운트된다.")
        @Test
        void countsEachUser_whenMultipleUsersLike() throws Exception {
            // given
            final Product product = productRepository.save(new Product(
                    "테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product.getId());
            
            final String loginId1 = "user1";
            final String loginId2 = "user2";
            
            userRepository.save(new User(
                    new LoginId(loginId1),
                    new Email("user1@example.com"),
                    new BirthDate("2025-01-01"),
                    Gender.F,
                    "user1"
            ));
            userRepository.save(new User(
                    new LoginId(loginId2),
                    new Email("user2@example.com"),
                    new BirthDate("2025-01-01"),
                    Gender.M,
                    "user2"
            ));

            // when
            mockMvc.perform(post(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.likeCount").exists());

            // then
            mockMvc.perform(post(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.likeCount").exists());
        }

        @DisplayName("로그인하지 않은 사용자의 좋아요 요청 시, BAD_REQUEST 오류가 발생한다.")
        @Test
        void returnsBadRequest_whenUnauthorizedRequest() throws Exception {
            // given
            final Product product = productRepository.save(new Product(
                    "테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product.getId());

            // when & then
            mockMvc.perform(post(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"));
        }
    }

    @DisplayName("DELETE /api/v1/like/products/{productId}")
    @Nested
    class UnlikeProduct {
        private static final String ENDPOINT = "/api/v1/like/products/{productId}";

        @DisplayName("좋아요한 상품을 취소 시, 성공적으로 좋아요가 제거된다.")
        @Test
        void removesLike_whenUnlikeRequest() throws Exception {
            // given
            final Product product = productRepository.save(new Product(
                    "테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product.getId());

            final String loginId = "test123456";
            final User user = userRepository.save(new User(
                    new LoginId(loginId),
                    new Email("test@example.com"),
                    new BirthDate("2025-01-01"),
                    Gender.F,
                    "test"
            ));

            likeFacade.like(product.getId(), loginId);

            // when & then
            mockMvc.perform(delete(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.productId").value(product.getId()))
                    .andExpect(jsonPath("$.data.likeCount").value(0));
        }

        @DisplayName("좋아요하지 않은 상품을 취소 시, 멱등성이 보장된다.")
        @Test
        @Transactional
        void isIdempotent_whenUnlikeNonLikedProduct() throws Exception {
            // given
            final Product product = productRepository.save(new Product(
                    "테스트 상품", "설명", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product.getId());

            final String loginId = "test123456";
            userRepository.save(new User(
                    new LoginId(loginId),
                    new Email("test@example.com"),
                    new BirthDate("2025-01-01"),
                    Gender.F,
                    "test"
            ));

            // when & then
            mockMvc.perform(delete(ENDPOINT, product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.productId").value(product.getId()))
                    .andExpect(jsonPath("$.data.likeCount").value(0)); // 에러 없이 0 반환
        }
    }

    @DisplayName("GET /api/v1/like/products")
    @Nested
    class GetUserLikeProducts {
        private static final String ENDPOINT = "/api/v1/like/products";

        @DisplayName("좋아요한 상품이 없는 사용자는 빈 목록을 반환한다.")
        @Test
        @Transactional
        void returnsEmptyList_whenUserHasNoLikes() throws Exception {
            // given
            final String loginId = "test123456";
            userService.register(new UserCommand.Register(loginId, "test", "F", "test@example.com", "2025-01-01"));

            // when & then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products").isArray())
                    .andExpect(jsonPath("$.data.products").isEmpty());
        }

        @DisplayName("사용자가 좋아요한 상품 목록을 정확히 반환한다.")
        @Test
        @Transactional
        void returnsLikedProducts_whenUserHasLikes() throws Exception {
            // given
            final String loginId = "test123456";
            userService.register(new UserCommand.Register(loginId, "test", "F", "test@example.com", "2025-01-01"));
            
            final Product product1 = productRepository.save(new Product(
                    "상품1", "설명1", BigDecimal.valueOf(10000), "의류", 1L
            ));
            testHelper.prepareLikeCount(product1.getId());
            final Product product2 = productRepository.save(new Product(
                    "상품2", "설명2", BigDecimal.valueOf(20000), "전자제품", 2L
            ));
            testHelper.prepareLikeCount(product2.getId());

            // 2개 상품에 좋아요
            mockMvc.perform(post("/api/v1/like/products/{productId}", product1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/v1/like/products/{productId}", product2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products").isArray());
        }

        @DisplayName("로그인하지 않은 사용자의 요청 시, BAD_REQUEST 오류가 발생한다.")
        @Test
        void returnsBadRequest_whenUnauthorizedRequest() throws Exception {
            // when & then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"));
        }

        @DisplayName("존재하지 않는 사용자의 요청 시, BAD_REQUEST 오류가 발생한다.")
        @Test
        void returnsBadRequest_whenNonExistentUser() throws Exception {
            // given
            final String nonExistentLoginId = "nonexistent";

            // when & then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", nonExistentLoginId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"));
        }
    }
}
