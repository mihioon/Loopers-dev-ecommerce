package com.loopers.infrastructure.kafka.idempotency;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "event_handled")
@IdClass(ProcessedEventId.class)
public class ProcessedEvent {
    
    @Id
    @Column(name = "event_id", length = 36)
    private String eventId;
    
    @Id
    @Column(name = "consumer_group", length = 100)
    private String consumerGroup;
    
    @Column(name = "handled_at", nullable = false)
    private Instant handledAt;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;
    
    protected ProcessedEvent() {}
    
    public ProcessedEvent(String eventId, String consumerGroup) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
        this.handledAt = Instant.now();
        this.retryCount = 0;
    }
    
    public ProcessedEvent(String eventId, String consumerGroup, Instant handledAt, Integer retryCount) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
        this.handledAt = handledAt;
        this.retryCount = retryCount;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getConsumerGroup() {
        return consumerGroup;
    }
    
    public Instant getHandledAt() {
        return handledAt;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
}
