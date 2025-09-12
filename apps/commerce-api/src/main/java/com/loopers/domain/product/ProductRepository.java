package com.loopers.domain.product;

import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.domain.product.dto.ProductWithLikeCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    Product save(Product product);

    void deleteById(Long id);

    Optional<Product> findById(Long id);

    List<Product> findByIds(List<Long> ids);

    Page<ProductWithLikeCountProjection> findProductsWithSort(ProductQuery.Summary command, Pageable pageable);
    
    List<ProductWithLikeCountProjection> findProductsWithLikeCountByIds(Set<Long> productIds);

    long countProductsWithFilter(String category, Long brandId);
    
    Optional<Product> findByIdWithImagesAndDetail(Long id);

    ProductStock save(ProductStock productStock);

    Optional<ProductStock> findStockByProductId(Long productId);

    Optional<ProductStock> findStockByProductIdWithLock(Long productId);
}
