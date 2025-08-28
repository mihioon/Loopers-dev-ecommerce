package com.loopers.domain.common.event;

public interface EventPublisher {
    void publish(DomainEvent event);
}