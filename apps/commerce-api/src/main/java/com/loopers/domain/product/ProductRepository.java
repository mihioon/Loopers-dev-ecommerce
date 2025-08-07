package com.loopers.domain.product;

import com.loopers.domain.product.dto.ProductQuery;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);

    List<Product> findProductsWithSort(ProductQuery.Summary command, Pageable pageable);
    long countProductsWithFilter(String category, Long brandId);
    
    Optional<Product> findByIdWithImagesAndDetail(Long id);

    ProductStock save(ProductStock productStock);
    Optional<ProductStock> findStockByProductId(Long productId);
}
