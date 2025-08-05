package com.loopers.domain.stock;

import java.util.Optional;

public interface ProductStockRepository {
    ProductStock save(ProductStock productStock);
    Optional<ProductStock> findById(Long id);
    Optional<ProductStock> findByProductId(Long productId);
    void deleteById(Long id);
    void deleteByProductId(Long productId);
}
