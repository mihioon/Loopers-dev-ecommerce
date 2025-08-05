package com.loopers.interfaces.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.Balance;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductStock;
import com.loopers.domain.user.*;
import com.loopers.support.E2EIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderV1ApiE2ETest extends E2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("주문 생성 API 성공")
    @Test
    @Transactional
    void createOrder_Success() throws Exception {
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

        CreateOrder.V1.Request request = new CreateOrder.V1.Request(
                List.of(new CreateOrder.V1.Request.Item(productId, 2)),
                BigDecimal.ZERO
        );

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-USER-ID", "test1user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.totalAmount").value(20000))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.items[0].productId").value(productId))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].price").value(10000))
                .andExpect(jsonPath("$.data.items[0].totalPrice").value(20000));
    }

    @DisplayName("주문 조회 API 성공")
    @Test
    @Transactional
    void getOrder_Success() throws Exception {
        // given - 주문 생성
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

        CreateOrder.V1.Request createRequest = new CreateOrder.V1.Request(
                List.of(new CreateOrder.V1.Request.Item(productId, 1)),
                BigDecimal.ZERO
        );

        String createResponse = mockMvc.perform(post("/api/v1/orders")
                        .header("X-USER-ID", "test1user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = objectMapper.readTree(createResponse)
                .path("data")
                .path("orderId")
                .asLong();

        // when & then - 주문 조회
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.totalAmount").value(10000))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items", hasSize(1)));
    }

    @DisplayName("사용자 주문 목록 조회 API 성공")
    @Test
    @Transactional
    void getUserOrders_Success() throws Exception {
        // given - 주문 2개 생성
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

        CreateOrder.V1.Request request1 = new CreateOrder.V1.Request(
                List.of(new CreateOrder.V1.Request.Item(productId, 1)),
                BigDecimal.ZERO
        );
        CreateOrder.V1.Request request2 = new CreateOrder.V1.Request(
                List.of(new CreateOrder.V1.Request.Item(productId, 2)),
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-USER-ID", "test1user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-USER-ID", "test1user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // when & then - 주문 목록 조회
        mockMvc.perform(get("/api/v1/orders")
                        .header("X-USER-ID", "test1user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].orderId").exists())
                .andExpect(jsonPath("$.data[0].totalAmount").exists())
                .andExpect(jsonPath("$.data[1].orderId").exists())
                .andExpect(jsonPath("$.data[1].totalAmount").exists());
    }

    @DisplayName("재고 부족 시 주문 생성 실패")
    @Test
    @Transactional
    void createOrder_InsufficientStock() throws Exception {
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

        CreateOrder.V1.Request request = new CreateOrder.V1.Request(
                List.of(new CreateOrder.V1.Request.Item(productId, 5)), // 5개 주문
                BigDecimal.ZERO
        );

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-USER-ID", "test1user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.result").value("FAIL"))
                .andExpect(jsonPath("$.meta.message").value(containsString("재고가 부족합니다")));
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
