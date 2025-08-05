package com.loopers.application.product;

import com.loopers.domain.auth.AuthService;
import com.loopers.domain.product.ProductBrandService;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.like.LikeCountInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.dto.StockInfo;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.loopers.domain.like.LikeCommand;


@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final ProductBrandService productBrandService;
    private final ProductService stockService;
    private final LikeService likeService;
    private final AuthService authService;

    public ProductResult.Summary getProducts(final ProductCriteria.Summary criteria) {
        final Long userId = authService.resolveUserId(criteria.loginId()).orElse(null);

        // 상품 목록
        final ProductInfo.Summary productSummary = productBrandService.getSummary(criteria.toCommand());

        final LikeCountInfo likeCounts = likeService.getLikeCounts(LikeCommand.GetLikeCount.from(productSummary));
        final LikeCountInfo isLikedListByUser = userId == null
                ? null
                : likeService.getLikedListByUser(userId, productSummary.products().stream()
                        .map(ProductInfo.Summary.Item::id)
                        .toList());

        return ProductResult.Summary.from(productSummary, likeCounts, isLikedListByUser);
    }

    public ProductResult.Detail getProduct(final Long productId, final String loginId) {
        final Long userId = authService.resolveUserId(loginId).orElse(null);

        // 상품 정보
        final ProductInfo.Detail productDetail = productBrandService.getDetail(productId);

        final StockInfo stockInfo = stockService.getStock(productId);

        final Long likeCount = likeService.getLikeCount(productId);
        final Boolean isLikedByUser = userId != null && likeService.isLikedByUser(productId, userId);
        
        return ProductResult.Detail.from(productDetail, stockInfo, likeCount, isLikedByUser);
    }
}
