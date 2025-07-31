package com.loopers.application.product;

import com.loopers.domain.auth.AuthService;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeCountInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.*;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.loopers.domain.like.LikeCommand;


@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final ProductService productService;
    private final StockService stockService;
    private final LikeService likeService;
    private final AuthService authService;
    private final BrandService brandService;

    public ProductResult.Summery getProducts(final ProductCriteria.Summery criteria) {
        final Long userId = authService.resolveUserId(criteria.loginId()).orElse(null);

        // 상품 목록
        final ProductInfo.Summery productSummery = productService.getSummery(criteria.toCommand());

        final LikeCountInfo likeCounts = likeService.getLikeCounts(LikeCommand.GetLikeCount.from(productSummery));
        final LikeCountInfo isLikedListByUser = userId == null
                ? null
                : likeService.getLikedListByUser(userId, productSummery.products().stream()
                        .map(ProductInfo.Summery.Item::id)
                        .toList());

        return ProductResult.Summery.from(productSummery, likeCounts, isLikedListByUser);
    }

    public ProductResult.Detail getProduct(final Long productId, final String loginId) {
        final Long userId = authService.resolveUserId(loginId).orElse(null);

        // 상품 정보
        final ProductInfo.Detail productDetail = productService.getDetail(productId);

        final StockInfo stockInfo = stockService.getStock(productId);

        final Long likeCount = likeService.getLikeCount(productId);
        final Boolean isLikedByUser = userId != null && likeService.isLikedByUser(productId, userId);
        
        return ProductResult.Detail.from(productDetail, stockInfo, likeCount, isLikedByUser);
    }
}
