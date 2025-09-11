package com.loopers.infrastructure.kafka.idempotency;

import java.io.Serializable;
import java.util.Objects;

public class ProcessedEventId implements Serializable {
    
    private String eventId;
    private String consumerGroup;
    
    public ProcessedEventId() {
        // JPA
    }
    
    public ProcessedEventId(String eventId, String consumerGroup) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getConsumerGroup() {
        return consumerGroup;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessedEventId that)) return false;
        return Objects.equals(eventId, that.eventId) &&
               Objects.equals(consumerGroup, that.consumerGroup);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId, consumerGroup);
    }
}