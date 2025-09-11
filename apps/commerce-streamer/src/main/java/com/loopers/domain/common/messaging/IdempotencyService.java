package com.loopers.domain.common.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    
    private final IdempotencyRepository idempotencyRepository;
    
    public boolean isProcessed(String eventId, String consumerGroup) {
        return idempotencyRepository.existsByEventIdAndConsumerGroup(eventId, consumerGroup);
    }
    
    public void markAsProcessed(String eventId, String consumerGroup) {
        idempotencyRepository.save(eventId, consumerGroup);
    }
}
