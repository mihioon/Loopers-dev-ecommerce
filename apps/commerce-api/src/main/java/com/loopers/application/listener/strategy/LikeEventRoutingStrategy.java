package com.loopers.application.listener.strategy;

import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.like.event.LikeEvent;
import org.springframework.stereotype.Component;

@Component
public class LikeEventRoutingStrategy implements EventRoutingStrategy {
    
    @Override
    public boolean supports(Class<? extends DomainEvent> eventClass) {
        return LikeEvent.class.isAssignableFrom(eventClass);
    }
    
    @Override
    public MessageRoute route(DomainEvent event) {
        LikeEvent likeEvent = (LikeEvent) event;
        String stream = "like-events";
        String key = "product:" + likeEvent.getProductId();
        return MessageRoute.of(stream, key);
    }
}
