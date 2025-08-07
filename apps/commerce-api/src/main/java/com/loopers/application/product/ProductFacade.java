package com.loopers.application.product;

import com.loopers.domain.auth.AuthService;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.product.ProductStockService;
import com.loopers.domain.product.dto.ProductInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.dto.StockInfo;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.loopers.domain.like.LikeCommand;


@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final AuthService authService;
    private final ProductService productService;
    private final ProductStockService productStockService;
    private final BrandService brandService;
    private final LikeService likeService;

    public ProductResult.Summary getSummary(final ProductCriteria.Summary criteria) {
        final Long userId = authService.resolveUserId(criteria.loginId()).orElse(null);

        final Page<ProductInfo.Summary> productSummary = productService.getSummary(criteria.toCommand());

        final LikeInfo likeCounts = likeService.getLikeCounts(userId, LikeCommand.GetLikeCount.from(productSummary.getContent()));

        return ProductResult.Summary.from(productSummary, likeCounts);
    }

    public ProductResult.Detail getDetail(final Long productId, final String loginId) {
        final Long userId = authService.resolveUserId(loginId).orElse(null);

        final ProductInfo.Detail productDetail = productService.getDetail(productId);
        final BrandInfo brandInfo = brandService.getBy(productDetail.brandId());
        final StockInfo stockInfo = productStockService.getStock(productId);
        final Long likeCount = likeService.getLikeCount(productId);
        final Boolean isLikedByUser = userId != null && likeService.isLikedByUser(productId, userId);
        
        return ProductResult.Detail.from(productDetail, brandInfo, stockInfo, likeCount, isLikedByUser);
    }
}
