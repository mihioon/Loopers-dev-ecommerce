package com.loopers.infrastructure.product;

import com.loopers.domain.catalog.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByBrandId(Long brandId);
    
    List<Product> findByCategory(String category);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:brandId IS NULL OR p.brandId = :brandId)")
    Page<Product> findProductsWithFilter(@Param("category") String category,
                                       @Param("brandId") Long brandId,
                                       Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:brandId IS NULL OR p.brandId = :brandId)")
    long countProductsWithFilter(@Param("category") String category,
                                @Param("brandId") Long brandId);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.detail WHERE p.id = :id")
    Optional<Product> findByIdWithDetail(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.detail WHERE p.id = :id")
    Optional<Product> findByIdWithImagesAndDetail(@Param("id") Long id);
}
