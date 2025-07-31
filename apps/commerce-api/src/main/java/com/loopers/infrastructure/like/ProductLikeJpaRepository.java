package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {
    
    Optional<ProductLike> findByProductIdAndUserId(Long productId, Long userId);
    
    List<ProductLike> findByUserId(Long userId);
    
    List<ProductLike> findByProductIdIn(List<Long> productIds);
    
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    
    void deleteByProductIdAndUserId(Long productId, Long userId);
    
    @Query("SELECT COUNT(pl) FROM ProductLike pl WHERE pl.productId = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pl.productId, COUNT(pl) FROM ProductLike pl WHERE pl.productId IN :productIds GROUP BY pl.productId")
    List<Object[]> countByProductIds(@Param("productIds") List<Long> productIds);
}
