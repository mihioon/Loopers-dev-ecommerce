package com.loopers.application.like;

import com.loopers.domain.auth.AuthService;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeFacade {
    private final LikeService likeService;

    private final AuthService authService;

    public void like(final Long productId, final String loginId) {
        Long userId = authService.requireUserId(loginId);

        likeService.like(new LikeCommand.Like(productId, userId));
    }

    public void unlike(final Long productId, final String loginId) {
        Long userId = authService.requireUserId(loginId);

        likeService.unlike(new LikeCommand.Unlike(productId, userId));
    }

    public Long getLikeCount(final Long productId) {
        return likeService.getLikeCount(productId);
    }

    public LikeResult getUserLikeProducts(final String loginId) {
        Long userId = authService.requireUserId(loginId);

        return LikeResult.from(likeService.getUserLikeProducts(userId));
    }
}
