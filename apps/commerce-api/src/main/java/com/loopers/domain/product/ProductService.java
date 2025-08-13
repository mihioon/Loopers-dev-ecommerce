package com.loopers.domain.product;

import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductStatusRepository productStatusRepository;

    @Transactional(readOnly = true)
    public Page<ProductInfo.Summary> getSummary(final ProductQuery.Summary command) {
        final Pageable pageable = PageRequest.of(
                command.page(),
                command.size(),
                Sort.by(Sort.Direction.fromString(command.sortType().getDirection()), command.sortType().getField())
        );

        final List<Product> products = productRepository.findProductsWithSort(command, pageable);

        final long totalElements = productRepository.countProductsWithFilter(command.category(), command.brandId());

        final Map<Long, Long> productStatus = productStatusRepository.getLikeCountsFromCountTable(products.stream()
                .map(Product::getId)
                .toList());

        final List<ProductInfo.Summary> dtoList = products.stream()
                .map(product -> ProductInfo.Summary.from(
                        product,
                        productStatus.getOrDefault(product.getId(), 0L))
                )
                .toList();

        return new PageImpl<>(dtoList, pageable, totalElements);
    }

    public ProductInfo.Basic getBasic(final Long productId) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return ProductInfo.Basic.from(product);
    }

    public List<ProductInfo.Basic> getBasics(final List<Long> productIds) {
        return productRepository.findByIds(productIds).stream()
                .map(ProductInfo.Basic::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductInfo.Detail getDetail(final Long productId) {
        final Product product = productRepository.findByIdWithImagesAndDetail(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return ProductInfo.Detail.from(product);
    }

    @Transactional
    public void updateStatusLikeCount(final Long productId, final boolean increment) {
        ProductStatus likeCount = productStatusRepository.findWithLockByProductId(productId)
                .orElseThrow(() -> new IllegalStateException("ProductStatus row가 존재하지 않습니다. 미리 생성되어야 합니다."));

        if (increment) {
            likeCount.increase();
        } else {
            likeCount.decrease();
        }
    }

    @Transactional(readOnly = true)
    public Long getLikeCount(final Long productId) {
        return productStatusRepository.findLikeCountByProductId(productId)
                .map(ProductStatus::getLikeCount)
                .map(Long::valueOf)
                .orElse(0L);
    }
}
