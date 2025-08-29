package com.loopers.application.like;

import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductStatusRepository;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.support.IntegrationTest;
import com.loopers.support.TestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Like 서비스 간단한 동시성 테스트")
public class LikeFacadeConcurrencyTest extends IntegrationTest {

    @Autowired
    private LikeFacade sut;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private ProductStatusRepository productStatusRepository;

    @Autowired
    private TestHelper testHelper;

    final String loginIdPrefix = "test";

    @DisplayName("다수의 사용자가 동시에 같은 상품에 좋아요를 누를 때, " +
            "좋아요 저장 후 product_like_count.likeCount 값과 product_like row 수가 일치하고, 좋아요 수는 좋아요 요청 수와 일치한다.")
    @ParameterizedTest
    @ValueSource(ints = {100})
    void concurrentLikes_shouldMaintainCorrectCount_withCompletableFuture(int numberOfUsers) throws Exception {
        // given
        Long productId = 1L;
        testHelper.prepareLikeCount(productId);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        for (int i = 1; i <= numberOfUsers; i++) {
            String loginId = loginIdPrefix+i;
            userService.register(new UserCommand.Register(loginId, "test", "F", "test@example.com", "2025-01-01"));

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    sut.like(productId, loginId);
                } catch (Exception e) {
                    System.err.println("Like failed for user " + loginId + ": " + e.getMessage());
                }
            }, executorService);
            futures.add(future);
        }

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(10000, TimeUnit.SECONDS);

        executorService.shutdown();

        // then
        Long likeCountByProduct = productLikeRepository.getLikeCount(productId);
        assertThat(likeCountByProduct).isEqualTo((long) numberOfUsers);
    }

    @DisplayName("좋아요가 존재할 때, 동시에 좋아요 수 보다 적은 다수의 사용자가 좋아요를 취소할 때, " +
            "좋아요 저장 후 product_like_count.likeCount 값과 product_like row 수가 일치하고, 좋아요 수는 좋아요 요청 수와 일치한다.")
    @ParameterizedTest
    @MethodSource("provideLikeAndUnlikeUsers")
    void concurrentLikesAndUnlikes_shouldMaintainCorrectCount_withCompletableFuture(int likeUsers, int unlikeUsers) throws Exception {
        // given
        Long productId = 1L;
        testHelper.prepareLikeCount(productId);
        int expectedFinalCount = likeUsers - unlikeUsers;

        for (int i = 1; i <= likeUsers; i++) {
            String loginId = loginIdPrefix+i;
            userService.register(new UserCommand.Register(loginId, "test", "F", "test@example.com", "2025-01-01"));
            sut.like(productId, loginId);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        for (int i = 1; i <= unlikeUsers; i++) {
            String loginId = loginIdPrefix+i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    sut.unlike(productId, loginId);
                } catch (Exception e) {
                    System.err.println("Unlike failed for user " + loginId + ": " + e.getMessage());
                }
            }, executorService);
            futures.add(future);
        }

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(10000, TimeUnit.SECONDS);

        executorService.shutdown();

        // then
        Long likeCountByProduct = productLikeRepository.getLikeCount(productId);
        assertThat(likeCountByProduct).isEqualTo((long) expectedFinalCount);
    }

    private static Stream<Arguments> provideLikeAndUnlikeUsers() {
        return Stream.of(
                Arguments.of(5, 3),
                Arguments.of(100, 50),
                Arguments.of(1000, 1000)
        );
    }
}
