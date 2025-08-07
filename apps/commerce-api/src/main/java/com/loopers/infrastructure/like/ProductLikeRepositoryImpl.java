package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeCount;
import com.loopers.domain.like.ProductLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductLikeRepositoryImpl implements ProductLikeRepository {
    
    private final ProductLikeJpaRepository productLikeJpaRepository;
    private final ProductLikeCountJpaRepository productLikeCountJpaRepository;

    @Override
    public ProductLike save(ProductLike productLike) {
        return productLikeJpaRepository.save(productLike);
    }

    @Override
    public int deleteByProductIdAndUserId(Long productId, Long userId) {
        return productLikeJpaRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Optional<ProductLike> findById(Long id) {
        return productLikeJpaRepository.findById(id);
    }

    @Override
    public Long getLikeCount(Long productId) {
        return productLikeJpaRepository.countByProductId(productId);
    }

    @Override
    public List<ProductLike> findLikeCounts(List<Long> productIds) {
        return productLikeJpaRepository.findByProductIdIn(productIds);
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

    @Override
    public Set<Long> getLikedProductIds(Long userId, List<Long> productIds) {
        return productLikeJpaRepository.getLikedProductIds(productIds, userId);
    }

    @Override
    public boolean isLikedByUser(Long productId, Long userId) {
        return productLikeJpaRepository.existsByProductIdAndUserId(productId, userId);
    }

    @Override
    public List<ProductLike> findByUserId(Long userId) {
        return productLikeJpaRepository.findByUserId(userId);
    }
    
    // ProductLikeCount 관련 메서드들
    @Override
    public ProductLikeCount save(ProductLikeCount productLikeCount) {
        return productLikeCountJpaRepository.save(productLikeCount);
    }
    
    @Override
    public Optional<ProductLikeCount> findLikeCountByProductId(Long productId) {
        return productLikeCountJpaRepository.findByProductId(productId);
    }
    
    @Override
    public Map<Long, Long> getLikeCountsFromCountTable(List<Long> productIds) {
        List<ProductLikeCount> likeCounts = productLikeCountJpaRepository.findByProductIdIn(productIds);
        
        return likeCounts.stream()
                .collect(Collectors.toMap(
                        ProductLikeCount::getProductId,
                        count -> Long.valueOf(count.getLikeCount())
                ));
    }
}
