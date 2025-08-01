package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeTransactionHelper {
    private final ProductLikeRepository productLikeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveLike(final LikeCommand.Like command) {
        productLikeRepository.save(new ProductLike(command.productId(), command.userId()));
    }
}
