package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

interface ProductStatusJpaRepository extends JpaRepository<ProductStatus, Long> {
    Optional<ProductStatus> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT plc FROM ProductStatus plc WHERE plc.productId = :productId")
    Optional<ProductStatus> findWithLockByProductId(@Param("productId") Long productId);

    @Query("SELECT plc FROM ProductStatus plc WHERE plc.productId IN :productIds")
    List<ProductStatus> findByProductIdIn(@Param("productIds") List<Long> productIds);

    void deleteByProductId(Long productId);
}
