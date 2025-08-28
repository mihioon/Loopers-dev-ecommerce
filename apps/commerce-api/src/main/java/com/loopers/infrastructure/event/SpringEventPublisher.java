package com.loopers.infrastructure.event;

import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.common.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SpringEventPublisher implements EventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing event: {}", event);
        
        try {
            applicationEventPublisher.publishEvent(event);
            log.info("Successfully published event: eventId={}, eventType={}", 
                    event.getEventId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish event: eventId={}, eventType={}, error={}", 
                    event.getEventId(), event.getEventType(), e.getMessage(), e);
            throw new RuntimeException("이벤트 발행에 실패했습니다.", e);
        }
    }
}