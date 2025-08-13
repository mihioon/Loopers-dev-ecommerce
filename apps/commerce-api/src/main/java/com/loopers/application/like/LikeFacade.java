package com.loopers.application.like;

import com.loopers.domain.auth.AuthService;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeFacade {
    private final LikeService likeService;

    private final AuthService authService;

    private final ProductService productService;

    @Transactional(noRollbackFor = {CoreException.class, DataIntegrityViolationException.class})
    public void like(final Long productId, final String loginId) {
        Long userId = authService.requireUserId(loginId);

        try {
            likeService.like(new LikeCommand.Like(productId, userId));
            productService.updateStatusLikeCount(productId, true);
        } catch (DataIntegrityViolationException e) {
            return;
        }
    }

    @Transactional
    public void unlike(final Long productId, final String loginId) {
        Long userId = authService.requireUserId(loginId);

        if (likeService.unlike(new LikeCommand.Unlike(productId, userId))) {
            productService.updateStatusLikeCount(productId, false);
        }
    }

    public Long getLikeCount(final Long productId) {
        return productService.getLikeCount(productId);
    }

    public LikeResult getUserLikeProducts(final String loginId) {
        Long userId = authService.requireUserId(loginId);

        return LikeResult.from(likeService.getUserLikeProducts(userId));
    }
}
