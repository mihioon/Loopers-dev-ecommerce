package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.ProductWithLikeCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

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
    
    List<ProductWithLikeCountProjection> findProductsWithLikeCountByIds(Set<Long> productIds);
}
