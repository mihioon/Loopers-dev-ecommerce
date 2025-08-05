package com.loopers.application.order;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.point.Balance;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductStock;
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

public class OrderFacadeIntegrationTest extends IntegrationTest {

    @Autowired
    private OrderFacade orderFacade;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PointRepository pointRepository;

    @DisplayName("정상 주문 생성 통합 테스트")
    @Test
    @Transactional
    void createOrder_Success() {
        // given - 테스트 데이터 준비
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        Product product = createTestProduct();
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        
        ProductStock stock = new ProductStock(productId, 100);
        productRepository.save(stock);
        
        // 포인트 데이터 추가
        Point point = new Point(userId, new Balance(10000L));
        pointRepository.save(point);

        OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 2);
        OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO);

        // when
        OrderResult.Detail result = orderFacade.createOrder(criteria);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("20000"));
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).productId()).isEqualTo(productId);
        assertThat(result.items().get(0).quantity()).isEqualTo(2);
        assertThat(result.items().get(0).price()).isEqualTo(new BigDecimal("10000"));
    }

    @DisplayName("재고 부족 시 주문 실패 통합 테스트")
    @Test
    @Transactional
    void createOrder_StockInsufficient() {
        // given
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        Product product = createTestProduct();
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        
        ProductStock stock = new ProductStock(productId, 1); // 재고 1개만
        productRepository.save(stock);
        
        // 포인트 데이터 추가
        Point point = new Point(userId, new Balance(10000L));
        pointRepository.save(point);

        OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 5); // 5개 주문
        OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO);

        // when & then
        CoreException exception = assertThrows(CoreException.class, () -> {
            orderFacade.createOrder(criteria);
        });
        
        assertThat(exception.getMessage()).contains("재고가 부족합니다");
    }

    @DisplayName("상품을 찾을 수 없을 때 주문 실패 통합 테스트")
    @Test
    @Transactional
    void createOrder_ProductNotFound() {
        // given
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        // 포인트 데이터 추가
        Point point = new Point(userId, new Balance(10000L));
        pointRepository.save(point);
        
        OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(999L, 1); // 존재하지 않는 상품 ID
        OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO);

        // when & then
        CoreException exception = assertThrows(CoreException.class, () -> {
            orderFacade.createOrder(criteria);
        });
        
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(exception.getMessage()).contains("상품을 찾을 수 없습니다");
    }


    @DisplayName("주문 조회 통합 테스트")
    @Test
    @Transactional
    void getOrder_Success() {
        // given
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        Product product = createTestProduct();
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        
        ProductStock stock = new ProductStock(productId, 100);
        productRepository.save(stock);
        
        // 포인트 데이터 추가
        Point point = new Point(userId, new Balance(10000L));
        pointRepository.save(point);

        // 주문 생성
        OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 2);
        OrderCriteria.Create createCriteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO);
        OrderResult.Detail createdOrder = orderFacade.createOrder(createCriteria);

        // when
        OrderResult.Detail result = orderFacade.getOrder(createdOrder.id());

        // then
        assertThat(result.id()).isEqualTo(createdOrder.id());
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("20000"));
        assertThat(result.items()).hasSize(1);
    }

    @DisplayName("사용자 주문 목록 조회 통합 테스트")
    @Test
    @Transactional
    void getUserOrders_Success() {
        // given
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        
        Product product = createTestProduct();
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();
        
        ProductStock stock = new ProductStock(productId, 100);
        productRepository.save(stock);
        
        // 포인트 데이터 추가
        Point point = new Point(userId, new Balance(50000L));
        pointRepository.save(point);

        // 주문 2개 생성
        OrderCriteria.Create.Item item1 = new OrderCriteria.Create.Item(productId, 1);
        OrderCriteria.Create createCriteria1 = new OrderCriteria.Create(userId, List.of(item1), BigDecimal.ZERO);
        orderFacade.createOrder(createCriteria1);
        
        OrderCriteria.Create.Item item2 = new OrderCriteria.Create.Item(productId, 2);
        OrderCriteria.Create createCriteria2 = new OrderCriteria.Create(userId, List.of(item2), BigDecimal.ZERO);
        orderFacade.createOrder(createCriteria2);

        // when
        List<OrderResult.Detail> results = orderFacade.getUserOrders(userId);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).userId()).isEqualTo(userId);
        assertThat(results.get(1).userId()).isEqualTo(userId);
    }

    private User createTestUser() {
        return new User(
                new LoginId("test1user"),
                new Email("test@test.com"),
                new BirthDate("1990-01-01"),
                Gender.M,
                "테스트사용자"
        );
    }

    private Product createTestProduct() {
        Product product = new Product(
                "테스트 상품",
                "테스트 상품 설명",
                new BigDecimal("10000"),
                "TEST_CATEGORY",
                1L
        );
        
        // ProductDetail 추가 (ProductService.getDetail에서 필요)
        Product.ProductDetail detail = new Product.ProductDetail(null, "테스트 상품 상세 설명");
        product.setDetail(detail);
        
        return product;
    }
}
