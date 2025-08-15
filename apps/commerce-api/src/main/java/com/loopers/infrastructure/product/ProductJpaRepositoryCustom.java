package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.ProductWithLikeCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductJpaRepositoryCustom {
    
    Page<ProductWithLikeCountProjection> findProductsWithFilter(
            String category,
            Long brandId,
            Pageable pageable
    );
    
    Page<ProductWithLikeCountProjection> findProductsWithFilterByLikes(
            String category,
            Long brandId,
            Pageable pageable
    );
    
    long countProductsWithFilter(String category, Long brandId);
}
