package com.loopers.domain.order;

import com.loopers.domain.catalog.product.Product;
import com.loopers.domain.catalog.product.ProductRepository;
import com.loopers.domain.user.*;
import com.loopers.support.IntegrationTest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

    @DisplayName("주문 생성 통합 테스트")
    @Test
    @Transactional
    void createOrder_Success() {
        // given - 테스트 데이터 준비
        User user = createTestUser(1L);
        userRepository.save(user);
        
        Product product = createTestProduct(1L, "테스트 상품", new BigDecimal("10000"));
        productRepository.save(product);

        // 주문 생성
        List<OrderCommand.Create.Item> commandItems = List.of(
                new OrderCommand.Create.Item(1L, 2)
        );
        OrderCommand.Create command = new OrderCommand.Create(1L, commandItems, BigDecimal.ZERO);

        // when - 주문 생성
        OrderInfo.Detail orderResult = orderService.createOrder(command);

        // then - 주문 생성 검증
        assertThat(orderResult.userId()).isEqualTo(1L);
        assertThat(orderResult.totalAmount()).isEqualTo(new BigDecimal("20000"));
        assertThat(orderResult.items()).hasSize(1);
        
        // DB에서 주문 확인
        Order savedOrder = orderRepository.findById(orderResult.id()).orElseThrow();
        assertThat(savedOrder.getUserId()).isEqualTo(1L);
        assertThat(savedOrder.getTotalAmount()).isEqualTo(new BigDecimal("20000"));
        assertThat(savedOrder.getOrderItems()).hasSize(1);
    }

    @DisplayName("주문 조회 통합 테스트")
    @Test
    @Transactional
    void getOrder_Success() {
        // given - 주문 데이터 생성
        User user = createTestUser(1L);
        userRepository.save(user);
        
        Product product = createTestProduct(1L, "테스트 상품", new BigDecimal("10000"));
        productRepository.save(product);

        List<OrderCommand.Create.Item> commandItems = List.of(
                new OrderCommand.Create.Item(1L, 1)
        );
        OrderCommand.Create command = new OrderCommand.Create(1L, commandItems, BigDecimal.ZERO);
        OrderInfo.Detail createdOrder = orderService.createOrder(command);

        // when - 주문 조회
        OrderInfo.Detail result = orderService.getOrder(createdOrder.id());

        // then
        assertThat(result.id()).isEqualTo(createdOrder.id());
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("10000"));
        assertThat(result.items()).hasSize(1);
    }

    @DisplayName("사용자 주문 목록 조회 통합 테스트")
    @Test
    @Transactional
    void getUserOrders_Success() {
        // given - 여러 주문 생성
        User user = createTestUser(1L);
        userRepository.save(user);
        
        Product product1 = createTestProduct(1L, "상품1", new BigDecimal("10000"));
        Product product2 = createTestProduct(2L, "상품2", new BigDecimal("20000"));
        productRepository.save(product1);
        productRepository.save(product2);

        // 첫 번째 주문
        List<OrderCommand.Create.Item> commandItems1 = List.of(
                new OrderCommand.Create.Item(1L, 1)
        );
        OrderCommand.Create command1 = new OrderCommand.Create(1L, commandItems1, BigDecimal.ZERO);
        orderService.createOrder(command1);

        // 두 번째 주문
        List<OrderCommand.Create.Item> commandItems2 = List.of(
                new OrderCommand.Create.Item(2L, 1)
        );
        OrderCommand.Create command2 = new OrderCommand.Create(1L, commandItems2, BigDecimal.ZERO);
        orderService.createOrder(command2);

        // when - 사용자 주문 목록 조회
        List<OrderInfo.Detail> results = orderService.getUserOrders(1L);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).userId()).isEqualTo(1L);
        assertThat(results.get(1).userId()).isEqualTo(1L);
    }

    @DisplayName("존재하지 않는 주문 조회 시 예외")
    @Test
    @Transactional
    void getOrder_NotFound() {
        // when & then
        CoreException exception = assertThrows(CoreException.class, () -> {
            orderService.getOrder(999L);
        });

        assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(exception.getMessage()).contains("주문을 찾을 수 없습니다");
    }

    private User createTestUser(Long userId) {
        return new User(
                new LoginId("test" + userId + "u"),
                new Email("test" + userId + "@test.com"),
                new BirthDate("1990-01-01"),
                Gender.M,
                "테스트사용자" + userId
        );
    }

    private Product createTestProduct(Long productId, String name, BigDecimal price) {
        return new Product(
                name,
                "테스트 상품 설명",
                price,
                "TEST_CATEGORY",
                1L
        );
    }
}
