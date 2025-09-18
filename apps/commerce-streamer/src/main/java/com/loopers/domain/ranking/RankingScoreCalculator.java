package com.loopers.domain.ranking;

import com.loopers.events.like.LikeAddedEvent;
import com.loopers.events.like.LikeRemovedEvent;
import com.loopers.events.order.OrderCompletedEvent;
import com.loopers.events.product.ProductViewedEvent;
import org.springframework.stereotype.Component;

@Component
public class RankingScoreCalculator {
    
    private static final double VIEW_WEIGHT = 0.1;
    private static final double LIKE_WEIGHT = 0.2;
    private static final double ORDER_WEIGHT = 0.6;
    
    public double calculateViewScore(ProductViewedEvent event) {
        return VIEW_WEIGHT;
    }

    public double calculateLikeAddedScore(LikeAddedEvent event) {
        return LIKE_WEIGHT;
    }

    public double calculateLikeRemovedScore(LikeRemovedEvent event) {
        return -LIKE_WEIGHT;
    }

    public double calculateOrderItemScore(OrderCompletedEvent.OrderItemInfo item) {
        return item.getPrice().doubleValue() * item.getQuantity() * ORDER_WEIGHT;
    }
}
