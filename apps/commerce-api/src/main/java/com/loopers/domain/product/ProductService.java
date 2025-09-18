package com.loopers.domain.product;

import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.product.dto.ProductQuery;
import com.loopers.domain.product.dto.ProductCommand;
import com.loopers.domain.product.dto.ProductWithLikeCountProjection;
import com.loopers.domain.ranking.ProductRankingInfo;
import com.loopers.domain.ranking.RankingQueryService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductStatusRepository productStatusRepository;
    private final ProductCacheRepository productCacheRepository;
    private final RankingQueryService rankingQueryService;

    @Transactional(readOnly = true)
    public Page<ProductInfo.Summary> getSummary(final ProductQuery.Summary command) {
        final Pageable pageable = createPageableWithSort(command);

        final Page<ProductWithLikeCountProjection> productsWithLikes =
                productRepository.findProductsWithSort(command, pageable);

        final List<ProductInfo.Summary> dtoList = productsWithLikes.getContent().stream()
                .map(ProductInfo.Summary::from)
                .toList();

        final long totalElementCount = countProductsWithFilter(command.category(), command.brandId());

        return new PageImpl<>(dtoList, pageable, totalElementCount);
    }

    public long countProductsWithFilter(String category, Long brandId) {
        String cacheKey = createCacheKey(category, brandId);

        Long cachedCount = productCacheRepository.get(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }

        long totalElementCount = productRepository.countProductsWithFilter(category, brandId);
        productCacheRepository.set(cacheKey, totalElementCount, Duration.ofHours(1));

        return totalElementCount;
    }

    @Transactional
    public Long createProduct(ProductCommand.Create command) {
        Product product = new Product(
                command.name(),
                command.description(),
                command.price(),
                command.category(),
                command.brandId()
        );
        
        Product savedProduct = productRepository.save(product);
        
        ProductStatus initialStatus = new ProductStatus(savedProduct.getId());
        productStatusRepository.save(initialStatus);

        // TODO: 이외 연관된 엔티티 생성

        // 캐시 무효화
        evictProductCountCache(command.category(), command.brandId());

        return savedProduct.getId();
    }
    
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        
        productStatusRepository.deleteByProductId(productId);
        productRepository.deleteById(productId);

        // TODO: 이외 연관된 엔티티 삭제

        // 캐시 무효화
        evictProductCountCache(product.getCategory(), product.getBrandId());
    }

    public String createCacheKey(String category, Long brandId) {
        return String.format("product_count:category=%s:brand=%s", category, brandId);
    }

    public void evictProductCountCache(String category, Long brandId) {
        productCacheRepository.delete(createCacheKey(category, brandId));
    }

    public ProductInfo.Basic getBasic(final Long productId) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return ProductInfo.Basic.from(product);
    }

    public List<ProductInfo.Basic> getBasics(final List<Long> productIds) {
        List<Product> products = productRepository.findByIds(productIds);

        if (products.size() != productIds.size()) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다");
        }

        if(products.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다");
        }

        return products.stream()
                .map(ProductInfo.Basic::from)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Map<Long, ProductInfo.Summary> getProductSummaries(final Set<Long> productIds) {
        if (productIds.isEmpty()) {
            return Map.of();
        }
        
        List<ProductWithLikeCountProjection> productsWithLikes = 
                productRepository.findProductsWithLikeCountByIds(productIds);
        
        return productsWithLikes.stream()
                .map(projection -> ProductInfo.Summary.from(projection.getProduct(), projection.getLikeCount()))
                .collect(Collectors.toMap(
                        ProductInfo.Summary::id,
                        Function.identity()
                ));
    }

    @Transactional(readOnly = true)
    public ProductInfo.Detail getDetail(final Long productId) {
        final Product product = productRepository.findByIdWithImagesAndDetail(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        Optional<ProductRankingInfo> rankingInfo = rankingQueryService.getProductRanking(productId, LocalDate.now());
        ProductInfo.RankingInfo ranking = rankingInfo.map(ProductInfo.RankingInfo::from).orElse(null);

        return ProductInfo.Detail.from(product, ranking);
    }

    @Transactional
    public void updateStatusLikeCount(final Long productId, final boolean increment) {
        ProductStatus likeCount = productStatusRepository.findWithLockByProductId(productId)
                .orElseThrow(() -> new IllegalStateException("ProductStatus row가 존재하지 않습니다. 미리 생성되어야 합니다."));

        if (increment) {
            likeCount.increase();
        } else {
            likeCount.decrease();
        }
    }

    @Transactional(readOnly = true)
    public Long getLikeCount(final Long productId) {
        return productStatusRepository.findLikeCountByProductId(productId)
                .map(ProductStatus::getLikeCount)
                .map(Long::valueOf)
                .orElse(0L);
    }
    
    private Pageable createPageableWithSort(ProductQuery.Summary command) {
        return switch (command.sortType()) {
            case LATEST -> createPageable(command, "createdAt", Sort.Direction.DESC);
            case PRICE_DESC -> createPageable(command, "price", Sort.Direction.DESC);
            case PRICE_ASC -> createPageable(command, "price", Sort.Direction.ASC);
            case LIKES_DESC -> PageRequest.of(command.page(), command.size()); // JPA 쿼리에서 ORDER BY 처리
        };
    }
    
    private Pageable createPageable(ProductQuery.Summary command, String property, Sort.Direction direction) {
        Sort sort = Sort.by(direction, property).and(Sort.by(direction, "id"));
        return PageRequest.of(command.page(), command.size(), sort);
    }
}
