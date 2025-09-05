package com.loopers.application.listener.strategy;

import com.loopers.domain.common.event.DomainEvent;

public interface EventRoutingStrategy {
    
    boolean supports(Class<? extends DomainEvent> eventClass);
    
    MessageRoute route(DomainEvent event);
}
