package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductStatusRepositoryImpl implements ProductStatusRepository {
    private final ProductStatusJpaRepository ProductStatusJpaRepository;

    @Override
    public ProductStatus save(ProductStatus ProductStatus) {
        return ProductStatusJpaRepository.save(ProductStatus);
    }

    @Override
    public ProductStatus saveAndFlush(ProductStatus ProductStatus) {
        return ProductStatusJpaRepository.saveAndFlush(ProductStatus);
    }

    @Override
    public Optional<ProductStatus> findLikeCountByProductId(Long productId) {
        return ProductStatusJpaRepository.findByProductId(productId);
    }

    @Override
    public Optional<ProductStatus> findWithLockByProductId(Long productId) {
        return ProductStatusJpaRepository.findWithLockByProductId(productId);
    }

    @Override
    public Map<Long, Long> getLikeCountsFromCountTable(List<Long> productIds) {
        List<ProductStatus> likeCounts = ProductStatusJpaRepository.findByProductIdIn(productIds);

        return likeCounts.stream()
                .collect(Collectors.toMap(
                        ProductStatus::getProductId,
                        count -> Long.valueOf(count.getLikeCount())
                ));
    }
}
