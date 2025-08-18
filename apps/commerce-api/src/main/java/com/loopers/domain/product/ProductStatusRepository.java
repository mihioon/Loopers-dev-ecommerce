package com.loopers.domain.product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductStatusRepository {
    ProductStatus save(ProductStatus ProductStatus);

    ProductStatus saveAndFlush(ProductStatus ProductStatus);

    Optional<ProductStatus> findLikeCountByProductId(Long productId);

    Optional<ProductStatus> findWithLockByProductId(Long productId);

    Map<Long, Long> getLikeCountsFromCountTable(List<Long> productIds);

    void deleteByProductId(Long productId);
}
