package com.loopers.domain.catalog;

import com.loopers.domain.catalog.brand.Brand;
import com.loopers.domain.catalog.brand.BrandRepository;
import com.loopers.domain.catalog.product.Product;
import com.loopers.domain.catalog.product.ProductCommand;
import com.loopers.domain.catalog.product.ProductInfo;
import com.loopers.domain.catalog.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductBrandServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private ProductBrandService productBrandService;

    private Product product;
    private Brand brand;
    private Product.ProductDetail productDetail;

    @BeforeEach
    void setUp() {
        product = new Product(
                "테스트 상품",
                "상품 설명",
                new BigDecimal("10000"),
                "의류",
                1L
        );
        setId(product, 1L);
        
        productDetail = new Product.ProductDetail(1L, "상세 설명");
        setId(productDetail, 1L);
        product.setDetail(productDetail);
        
        Product.ProductImage productImage = new Product.ProductImage(1L, "image-url", Product.ImageType.MAIN);
        setId(productImage, 1L);
        product.addImage(productImage);
        
        brand = new Brand("테스트 브랜드", "test@brand.com");
        setId(brand, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID field", e);
        }
    }

    @DisplayName("상품 목록 조회가 정상적으로 동작한다")
    @Test
    void getSummery_Success() {
        // given
        ProductCommand.Summery command = new ProductCommand.Summery(
                null, null, ProductCommand.Summery.SortType.LATEST, 0, 10
        );
        
        List<Product> products = List.of(product);
        given(productRepository.findProductsWithSort(command)).willReturn(products);
        given(productRepository.countProductsWithFilter(null, null)).willReturn(1L);

        // when
        ProductInfo.Summery result = productBrandService.getSummery(command);

        // then
        assertThat(result.products()).hasSize(1);
        assertThat(result.currentPage()).isEqualTo(0);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        
        then(productRepository).should().findProductsWithSort(command);
        then(productRepository).should().countProductsWithFilter(null, null);
    }

    @DisplayName("상품 상세 조회가 정상적으로 동작한다")
    @Test
    void getDetail_Success() {
        // given
        Long productId = 1L;
        given(productRepository.findByIdWithImagesAndDetail(productId)).willReturn(Optional.of(product));
        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
        given(brandRepository.findBrandImagesByBrandId(1L)).willReturn(List.of());

        // when
        ProductInfo.Detail result = productBrandService.getDetail(productId);

        // then
        assertThat(result.id()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo("테스트 상품");
        assertThat(result.description()).isEqualTo("상품 설명");
        assertThat(result.price()).isEqualTo(new BigDecimal("10000"));
        assertThat(result.images()).hasSize(1);
        assertThat(result.detail()).isNotNull();
        assertThat(result.brandInfo()).isNotNull();
        
        then(productRepository).should().findByIdWithImagesAndDetail(productId);
        then(brandRepository).should().findById(1L);
    }

    @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
    @Test
    void getDetail_ProductNotFound() {
        // given
        Long productId = 999L;
        given(productRepository.findByIdWithImagesAndDetail(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productBrandService.getDetail(productId))
                .isInstanceOf(CoreException.class)
                .hasMessage("상품을 찾을 수 없습니다.")
                .extracting("errorType").isEqualTo(ErrorType.NOT_FOUND);
        
        then(brandRepository).should(never()).findById(anyLong());
    }

    @DisplayName("상품 상세 정보가 없을 때 예외가 발생한다")
    @Test
    void getDetail_ProductDetailNotFound() {
        // given
        Long productId = 1L;
        Product productWithoutDetail = new Product("상품", "설명", new BigDecimal("1000"), "카테고리", 1L);
        setId(productWithoutDetail, productId);
        
        given(productRepository.findByIdWithImagesAndDetail(productId)).willReturn(Optional.of(productWithoutDetail));
        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
        given(brandRepository.findBrandImagesByBrandId(1L)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> productBrandService.getDetail(productId))
                .isInstanceOf(CoreException.class)
                .hasMessage("상품 상세 정보를 찾을 수 없습니다.")
                .extracting("errorType").isEqualTo(ErrorType.NOT_FOUND);
    }

    @DisplayName("존재하지 않는 브랜드 조회 시 예외가 발생한다")
    @Test
    void getDetail_BrandNotFound() {
        // given
        Long productId = 1L;
        given(productRepository.findByIdWithImagesAndDetail(productId)).willReturn(Optional.of(product));
        given(brandRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productBrandService.getDetail(productId))
                .isInstanceOf(CoreException.class)
                .hasMessage("존재하지 않는 브랜드입니다.")
                .extracting("errorType").isEqualTo(ErrorType.NOT_FOUND);
    }

    @DisplayName("상품 기본 정보 조회가 정상적으로 동작한다")
    @Test
    void getBasic_Success() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductInfo.Basic result = productBrandService.getBasic(productId);

        // then
        assertThat(result.name()).isEqualTo("테스트 상품");
        assertThat(result.price()).isEqualTo(new BigDecimal("10000"));
        
        then(productRepository).should().findById(productId);
    }

    @DisplayName("존재하지 않는 상품의 기본 정보 조회 시 예외가 발생한다")
    @Test
    void getBasic_ProductNotFound() {
        // given
        Long productId = 999L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productBrandService.getBasic(productId))
                .isInstanceOf(CoreException.class)
                .hasMessage("상품을 찾을 수 없습니다.")
                .extracting("errorType").isEqualTo(ErrorType.NOT_FOUND);
    }
}
