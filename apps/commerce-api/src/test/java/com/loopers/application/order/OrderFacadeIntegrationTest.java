package com.loopers.application.order;

import com.loopers.domain.coupon.*;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
    
    @Autowired
    private CouponRepository couponRepository;

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
        OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO, List.of());

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
        OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO, List.of());

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
        OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO, List.of());

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
        OrderCriteria.Create createCriteria = new OrderCriteria.Create(userId, List.of(item), BigDecimal.ZERO, List.of());
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
        OrderCriteria.Create createCriteria1 = new OrderCriteria.Create(userId, List.of(item1), BigDecimal.ZERO, List.of());
        orderFacade.createOrder(createCriteria1);
        
        OrderCriteria.Create.Item item2 = new OrderCriteria.Create.Item(productId, 2);
        OrderCriteria.Create createCriteria2 = new OrderCriteria.Create(userId, List.of(item2), BigDecimal.ZERO, List.of());
        orderFacade.createOrder(createCriteria2);

        // when
        List<OrderResult.Detail> results = orderFacade.getUserOrders(userId);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).userId()).isEqualTo(userId);
        assertThat(results.get(1).userId()).isEqualTo(userId);
    }

    @DisplayName("원자성 테스트")
    @Nested
    class RollbackTest {
        @DisplayName("사용 불가능한 쿠폰이 포함된 경우, 주문 실패한다.")
        @Test
        @Transactional
        void createOrder_CouponInvalid_FailTest() {
            // given
            User user = createTestUser();
            User savedUser = userRepository.save(user);
            Long userId = savedUser.getId();

            Product product = createTestProduct();
            Product savedProduct = productRepository.save(product);
            Long productId = savedProduct.getId();

            ProductStock stock = new ProductStock(productId, 100);
            productRepository.save(stock);

            // 만료된 쿠폰 생성
            Coupon coupon = new Coupon(CouponType.FIXED, new BigDecimal("1000"), 10L);
            Coupon savedCoupon = couponRepository.save(coupon);
            IssuedCoupon expiredCoupon = new IssuedCoupon(savedCoupon, userId, ZonedDateTime.now().minusDays(1)); // 만료됨
            IssuedCoupon savedExpiredCoupon = couponRepository.save(expiredCoupon);

            Point point = new Point(userId, new Balance(30000L));
            pointRepository.save(point);

            OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 2);
            OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item),
                    BigDecimal.ZERO, List.of(savedExpiredCoupon.getId()));

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.createOrder(criteria);
            });

            assertThat(exception.getMessage()).contains("만료된 쿠폰입니다");

            // 롤백 검증: 재고가 차감되지 않아야 함
            ProductStock rollbackStock = productRepository.findStockByProductId(productId).orElseThrow();
            assertThat(rollbackStock.getQuantity()).isEqualTo(100);

            // 롤백 검증: 포인트가 차감되지 않아야 함
            Point rollbackPoint = pointRepository.findByUserId(userId).orElseThrow();
            assertThat(rollbackPoint.getBalance().getBalance()).isEqualTo(30000L);
        }

        @DisplayName("존재하지 않는 쿠폰이 포함된 경우, 주문 실패한다.")
        @Test
        @Transactional
        void createOrder_CouponNotFound_FailTest() {
            // given
            User user = createTestUser();
            User savedUser = userRepository.save(user);
            Long userId = savedUser.getId();

            Product product = createTestProduct();
            Product savedProduct = productRepository.save(product);
            Long productId = savedProduct.getId();

            ProductStock stock = new ProductStock(productId, 100);
            productRepository.save(stock);

            Point point = new Point(userId, new Balance(30000L));
            pointRepository.save(point);

            OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 2);
            OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item),
                    BigDecimal.ZERO, List.of(999L)); // 존재하지 않는 쿠폰 ID

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.createOrder(criteria);
            });

            assertThat(exception.getMessage()).contains("유효하지 않은 쿠폰이 포함되어 있습니다");

            // 롤백 검증: 재고가 차감되지 않아야 함
            ProductStock rollbackStock = productRepository.findStockByProductId(productId).orElseThrow();
            assertThat(rollbackStock.getQuantity()).isEqualTo(100);

            // 롤백 검증: 포인트가 차감되지 않아야 함
            Point rollbackPoint = pointRepository.findByUserId(userId).orElseThrow();
            assertThat(rollbackPoint.getBalance().getBalance()).isEqualTo(30000L);
        }

        @DisplayName("포인트가 부족한 경우, 주문에 실패하고 쿠폰 사용이 롤백된다.")
        @Test
        void createOrder_PointInsufficient_RollbackTest() {
            // given
            User user = createTestUser();
            User savedUser = userRepository.save(user);
            Long userId = savedUser.getId();

            Product product = createTestProduct();
            Product savedProduct = productRepository.save(product);
            Long productId = savedProduct.getId();

            ProductStock stock = new ProductStock(productId, 100);
            ProductStock savedStock = productRepository.save(stock);

            Coupon savedCoupon = couponRepository.save(new Coupon(CouponType.FIXED, new BigDecimal("2000"), 10L));
            IssuedCoupon savedIssuedCoupon = couponRepository.save(new IssuedCoupon(savedCoupon, userId, ZonedDateTime.now().plusDays(30)));

            // 포인트 부족 (1000원만)
            Point point = new Point(userId, new Balance(1000L));
            pointRepository.save(point);

            OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 2); // 20000원
            OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item),
                    new BigDecimal("18000"), List.of(savedIssuedCoupon.getId())); // 쿠폰 1000원 할인, 포인트 18000원 (부족)

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.createOrder(criteria);
            });

            assertThat(exception.getMessage()).contains("잔액이 부족합니다");

            // 롤백 검증: 쿠폰이 사용되지 않아야 함
            IssuedCoupon rollbackCoupon = couponRepository.findIssuedCouponById(savedIssuedCoupon.getId()).orElseThrow();
            assertThat(rollbackCoupon.isUsed()).isFalse();

            // 롤백 검증: 재고가 차감되지 않아야 함
            ProductStock rollbackStock = productRepository.findStockByProductId(productId).orElseThrow();
            assertThat(rollbackStock.getQuantity()).isEqualTo(100);
        }

        @DisplayName("재고 차감에 실패한 경우, 주문에 실패하고 쿠폰, 포인트가 롤백된다.")
        @Test
        void createOrder_StockReductionFailed_RollbackTest() {
            // given
            User user = createTestUser();
            User savedUser = userRepository.save(user);
            Long userId = savedUser.getId();

            Product product = createTestProduct();
            Product savedProduct = productRepository.save(product);
            Long productId = savedProduct.getId();

            ProductStock stock = new ProductStock(productId, 1); // 재고 1개만
            productRepository.save(stock);

            Coupon savedCoupon = couponRepository.save(new Coupon(CouponType.FIXED, new BigDecimal("2000"), 10L));
            IssuedCoupon savedIssuedCoupon = couponRepository.save(new IssuedCoupon(savedCoupon, userId, ZonedDateTime.now().plusDays(30)));

            Point point = new Point(userId, new Balance(30000L));
            pointRepository.save(point);

            OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 5); // 5개 주문 (재고 부족)
            OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item),
                    new BigDecimal("5000"), List.of(savedIssuedCoupon.getId()));

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.createOrder(criteria);
            });

            assertThat(exception.getMessage()).contains("재고가 부족합니다");

            // 롤백 검증: 쿠폰이 사용되지 않아야 함
            IssuedCoupon rollbackCoupon = couponRepository.findIssuedCouponById(savedIssuedCoupon.getId()).orElseThrow();
            assertThat(rollbackCoupon.isUsed()).isFalse();

            // 롤백 검증: 포인트가 차감되지 않아야 함
            Point rollbackPoint = pointRepository.findByUserId(userId).orElseThrow();
            assertThat(rollbackPoint.getBalance().getBalance()).isEqualTo(30000L);

            // 재고는 원래 수량 유지
            ProductStock rollbackStock = productRepository.findStockByProductId(productId).orElseThrow();
            assertThat(rollbackStock.getQuantity()).isEqualTo(1);
        }

        @DisplayName("주문이 성공한 경우, 모든 처리가 정상적으로 반영된다.")
        @Test
        @Transactional
        void createOrder_Success_AllProcessingApplied() {
            // given
            User user = createTestUser();
            User savedUser = userRepository.save(user);
            Long userId = savedUser.getId();

            Product product = createTestProduct();
            Product savedProduct = productRepository.save(product);
            Long productId = savedProduct.getId();

            productRepository.save(new ProductStock(productId, 100));

            Coupon savedCoupon = couponRepository.save(new Coupon(CouponType.FIXED, new BigDecimal("2000"), 10L));
            IssuedCoupon savedIssuedCoupon = couponRepository.save(new IssuedCoupon(savedCoupon, userId, ZonedDateTime.now().plusDays(30)));

            pointRepository.save(new Point(userId, new Balance(30000L)));

            OrderCriteria.Create.Item item = new OrderCriteria.Create.Item(productId, 2); // 20000원
            OrderCriteria.Create criteria = new OrderCriteria.Create(userId, List.of(item),
                    new BigDecimal("3000"), List.of(savedIssuedCoupon.getId())); // 쿠폰 2000원 + 포인트 3000원 = 5000원 할인

            // when
            OrderResult.Detail result = orderFacade.createOrder(criteria);

            // then
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.totalAmount()).isEqualTo(new BigDecimal("15000")); // 20000 - 2000 - 3000 = 15000

            // 성공 검증: 쿠폰이 사용되어야 함
            IssuedCoupon usedCoupon = couponRepository.findIssuedCouponById(savedIssuedCoupon.getId()).orElseThrow();
            assertThat(usedCoupon.isUsed()).isTrue();

            // 성공 검증: 포인트가 차감되어야 함
            Point updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            assertThat(updatedPoint.getBalance().getBalance()).isEqualTo(27000L); // 30000 - 3000

            // 성공 검증: 재고가 차감되어야 함
            ProductStock updatedStock = productRepository.findStockByProductId(productId).orElseThrow();
            assertThat(updatedStock.getQuantity()).isEqualTo(98); // 100 - 2
        }

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
        
        Product.ProductDetail detail = new Product.ProductDetail("테스트 상품 상세 설명");
        product.setDetail(detail);
        
        return product;
    }
}
