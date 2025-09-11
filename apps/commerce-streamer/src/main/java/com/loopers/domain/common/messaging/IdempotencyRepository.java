package com.loopers.domain.common.messaging;

public interface IdempotencyRepository {
    
    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
    
    void save(String eventId, String consumerGroup);
}