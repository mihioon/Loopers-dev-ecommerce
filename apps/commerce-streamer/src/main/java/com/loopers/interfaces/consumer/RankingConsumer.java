package com.loopers.interfaces.consumer;

import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.domain.common.messaging.IdempotencyService;
import com.loopers.domain.ranking.RankingScoreCalculator;
import com.loopers.domain.ranking.RankingService;
import com.loopers.events.common.DomainEvent;
import com.loopers.events.like.LikeAddedEvent;
import com.loopers.events.like.LikeRemovedEvent;
import com.loopers.events.order.OrderCompletedEvent;
import com.loopers.events.product.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingConsumer {
    
    private final RankingService rankingService;
    private final RankingScoreCalculator scoreCalculator;
    private final IdempotencyService idempotencyService;
    
    @KafkaListener(
        topics = {"product-view-events", "like-events", "order-events"},
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = ConsumerGroupConstants.RANKING
    )
    @Transactional
    public void handleEvents(List<ConsumerRecord<String, DomainEvent>> records, Acknowledgment ack) {
        log.debug("RankingConsumer received {} events", records.size());
        
        Map<LocalDate, Map<Long, Double>> scoresByDate = new HashMap<>();
        
        for (ConsumerRecord<String, DomainEvent> record : records) {
            DomainEvent event = record.value();
            
            if (idempotencyService.isProcessed(event.getEventId(), ConsumerGroupConstants.RANKING)) {
                log.debug("Event already processed by RankingConsumer: eventId={}", event.getEventId());
                continue;
            }
            
            try {
                processEvent(event, scoresByDate);
                idempotencyService.markAsProcessed(event.getEventId(), ConsumerGroupConstants.RANKING);
                
            } catch (Exception e) {
                log.error("Failed to process event in RankingConsumer: eventId={}", event.getEventId(), e);
                throw e;
            }
        }
        
        updateScoresBatch(scoresByDate);
        
        ack.acknowledge();
        log.debug("RankingConsumer processed {} events successfully", records.size());
    }
    
    private void processEvent(DomainEvent event, Map<LocalDate, Map<Long, Double>> scoresByDate) {
        LocalDate eventDate = event.getOccurredOn().toLocalDate();
        
        if (event instanceof ProductViewedEvent viewEvent) {
            double score = scoreCalculator.calculateViewScore(viewEvent);
            addScore(scoresByDate, eventDate, viewEvent.getProductId(), score);
            
        } else if (event instanceof LikeAddedEvent likeAddedEvent) {
            double score = scoreCalculator.calculateLikeAddedScore(likeAddedEvent);
            addScore(scoresByDate, eventDate, likeAddedEvent.getProductId(), score);
            
        } else if (event instanceof LikeRemovedEvent likeRemovedEvent) {
            double score = scoreCalculator.calculateLikeRemovedScore(likeRemovedEvent);
            addScore(scoresByDate, eventDate, likeRemovedEvent.getProductId(), score);
            
        } else if (event instanceof OrderCompletedEvent orderEvent) {
            processOrderEvent(orderEvent, scoresByDate, eventDate);
            
        } else {
            log.debug("Unsupported event type for ranking: {}", event.getEventType());
        }
    }
    
    private void processOrderEvent(OrderCompletedEvent orderEvent, 
                                   Map<LocalDate, Map<Long, Double>> scoresByDate, 
                                   LocalDate eventDate) {
        for (OrderCompletedEvent.OrderItemInfo item : orderEvent.getOrderItems()) {
            double itemScore = scoreCalculator.calculateOrderItemScore(item);
            addScore(scoresByDate, eventDate, item.getProductId(), itemScore);
        }
    }
    
    private void addScore(Map<LocalDate, Map<Long, Double>> scoresByDate, 
                         LocalDate date, Long productId, double score) {
        scoresByDate.computeIfAbsent(date, k -> new HashMap<>())
                   .merge(productId, score, Double::sum);
    }
    
    private void updateScoresBatch(Map<LocalDate, Map<Long, Double>> scoresByDate) {
        for (Map.Entry<LocalDate, Map<Long, Double>> dateEntry : scoresByDate.entrySet()) {
            LocalDate date = dateEntry.getKey();
            Map<Long, Double> productScores = dateEntry.getValue();
            
            if (!productScores.isEmpty()) {
                rankingService.updateProductScores(productScores, date);
                log.debug("Updated ranking scores for date: {}, products: {}", 
                         date, productScores.size());
            }
        }
    }
}
