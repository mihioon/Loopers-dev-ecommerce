package com.loopers.application.listener;

import com.loopers.application.listener.strategy.MessageRoute;
import com.loopers.application.listener.strategy.EventRoutingStrategy;
import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.common.messaging.MessageBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStreamListener {

    private final MessageBroker messageBroker;
    private final List<EventRoutingStrategy> routingStrategies;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDomainEvent(DomainEvent event) {
        EventRoutingStrategy strategy = findRoutingStrategy(event);
        
        if (strategy == null) {
            log.warn("No routing strategy found for event type: {}, skipping", event.getClass().getSimpleName());
            return;
        }
        
        MessageRoute route = strategy.route(event);
        
        try {
            messageBroker.publish(route.getStream(), route.getKey(), event);
            log.debug("Event published to stream: stream={}, key={}, eventId={}", 
                     route.getStream(), route.getKey(), event.getEventId());
            
        } catch (Exception e) {
            log.error("Event stream publish failed: stream={}, key={}, eventId={}, error={}", 
                     route.getStream(), route.getKey(), event.getEventId(), e.getMessage(), e);
        }
    }
    
    private EventRoutingStrategy findRoutingStrategy(DomainEvent event) {
        return routingStrategies.stream()
            .filter(strategy -> strategy.supports(event.getClass()))
            .findFirst()
            .orElse(null);
    }
}
