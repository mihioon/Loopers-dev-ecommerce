package com.loopers.application.listener;

import com.loopers.domain.like.event.LikeAddedEvent;
import com.loopers.domain.like.event.LikeRemovedEvent;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class LikeCountEventListener {
    
    private final ProductService productService;
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void handleLikeAdded(LikeAddedEvent event) {
        log.info("Processing like added event: productId={}, userId={}", 
                event.getProductId(), event.getUserId());
        
        try {
            productService.updateStatusLikeCount(event.getProductId(), true);
            log.info("Successfully increased like count: productId={}", event.getProductId());
        } catch (Exception e) {
            // 실패 시 로그만 기록, 복구 처리 없음
            log.error("Failed to increase like count: productId={}, error={}", 
                    event.getProductId(), e.getMessage(), e);
        }
    }
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void handleLikeRemoved(LikeRemovedEvent event) {
        log.info("Processing like removed event: productId={}, userId={}", 
                event.getProductId(), event.getUserId());
        
        try {
            productService.updateStatusLikeCount(event.getProductId(), false);
            log.info("Successfully decreased like count: productId={}", event.getProductId());
        } catch (Exception e) {
            // 실패 시 로그만 기록, 복구 처리 없음
            log.error("Failed to decrease like count: productId={}, error={}", 
                    event.getProductId(), e.getMessage(), e);
        }
    }
}
