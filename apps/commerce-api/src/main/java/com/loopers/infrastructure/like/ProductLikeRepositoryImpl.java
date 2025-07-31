package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductLikeRepositoryImpl implements ProductLikeRepository {
    
    private final ProductLikeJpaRepository productLikeJpaRepository;

    @Override
    public ProductLike save(ProductLike productLike) {
        return productLikeJpaRepository.save(productLike);
    }

    @Override
    public Optional<ProductLike> findById(Long id) {
        return productLikeJpaRepository.findById(id);
    }

    @Override
    public Long getLikeCount(Long productIds) {
        return productLikeJpaRepository.countByProductId(productIds);
    }

    public Map<Long, Long> getLikeCounts(List<Long> productIds) {
        List<Object[]> results = productLikeJpaRepository.countByProductIds(productIds);
        Map<Long, Long> likeCountMap = new HashMap<>();
        
        for (Object[] result : results) {
            Long productId = (Long) result[0];
            Long count = (Long) result[1];
            likeCountMap.put(productId, count);
        }
        
        // 좋아요가 없는 상품들은 0으로 설정
        for (Long productId : productIds) {
            likeCountMap.putIfAbsent(productId, 0L);
        }
        
        return likeCountMap;
    }

    public boolean isLikedByUser(Long productId, Long userId) {
        return productLikeJpaRepository.existsByProductIdAndUserId(productId, userId);
    }
}
