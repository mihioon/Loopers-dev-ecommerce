package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductLikeCountJpaRepository extends JpaRepository<ProductLikeCount, Long> {
    Optional<ProductLikeCount> findByProductId(Long productId);
    
    @Query("SELECT plc FROM ProductLikeCount plc WHERE plc.productId IN :productIds")
    List<ProductLikeCount> findByProductIdIn(@Param("productIds") List<Long> productIds);
}
