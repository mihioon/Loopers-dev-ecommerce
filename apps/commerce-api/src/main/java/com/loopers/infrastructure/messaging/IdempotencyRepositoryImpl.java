package com.loopers.infrastructure.messaging;

import com.loopers.domain.common.messaging.IdempotencyRepository;
import com.loopers.infrastructure.kafka.idempotency.ProcessedEvent;
import com.loopers.infrastructure.kafka.idempotency.ProcessedEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IdempotencyRepositoryImpl implements IdempotencyRepository {

    private final ProcessedEventJpaRepository processedEventJpaRepository;
    
    @Override
    public boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup) {
        return processedEventJpaRepository.existsByEventIdAndConsumerGroup(eventId, consumerGroup);
    }
    
    @Override
    public void save(String eventId, String consumerGroup) {
        ProcessedEvent processedEvent = new ProcessedEvent(eventId, consumerGroup);
        processedEventJpaRepository.save(processedEvent);
    }
}
