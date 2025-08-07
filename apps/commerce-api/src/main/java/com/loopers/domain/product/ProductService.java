package com.loopers.domain.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.domain.product.dto.ProductStockCommand;
import com.loopers.domain.product.dto.StockInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductService {
    
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductInfo.Summary> getSummary(final ProductQuery.Summary command) {
        final Pageable pageable = PageRequest.of(
                command.page(),
                command.size(),
                Sort.by(Sort.Direction.fromString(command.sortType().getDirection()), command.sortType().getField())
        );

        final List<Product> products = productRepository.findProductsWithSort(command, pageable);

        final long totalElements = productRepository.countProductsWithFilter(command.category(), command.brandId());

        final List<ProductInfo.Summary> dtoList = products.stream()
                .map(ProductInfo.Summary::from)
                .toList();

        return new PageImpl<>(dtoList, pageable, totalElements);
    }

    public ProductInfo.Basic getBasic(final Long productId) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return ProductInfo.Basic.from(product);
    }

    @Transactional(readOnly = true)
    public ProductInfo.Detail getDetail(final Long productId) {
        final Product product = productRepository.findByIdWithImagesAndDetail(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return ProductInfo.Detail.from(product);
    }
}
