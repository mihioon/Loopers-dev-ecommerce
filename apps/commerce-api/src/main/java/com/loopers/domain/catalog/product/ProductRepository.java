package com.loopers.domain.catalog.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);

    List<Product> findProductsWithSort(ProductCommand.Summery command);
    long countProductsWithFilter(String category, Long brandId);
    
    Optional<Product> findByIdWithImagesAndDetail(Long id);
}
