package com.loopers.domain.eventlog;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "event_log", indexes = {
    @Index(name = "idx_event_type_occurred", columnList = "event_type, occurred_on"),
    @Index(name = "idx_aggregate_id", columnList = "aggregate_id")
})
public class EventLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_id", length = 36, unique = true, nullable = false)
    private String eventId;
    
    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;
    
    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;
    
    @Column(name = "aggregate_id", length = 100, nullable = false)
    private String aggregateId;
    
    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload;
    
    @Column(name = "topic", length = 50)
    private String topic;
    
    @Column(name = "partition_id")
    private Integer partitionId;
    
    @Column(name = "offset_value")
    private Long offsetValue;
    
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
    
    protected EventLog() {
        // JPA
    }
    
    public EventLog(String eventId, String eventType, Instant occurredOn, 
                   String aggregateId, String payload, String topic, 
                   Integer partitionId, Long offsetValue) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.occurredOn = occurredOn;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.topic = topic;
        this.partitionId = partitionId;
        this.offsetValue = offsetValue;
        this.processedAt = Instant.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    public String getAggregateId() {
        return aggregateId;
    }
    
    public String getPayload() {
        return payload;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public Integer getPartitionId() {
        return partitionId;
    }
    
    public Long getOffsetValue() {
        return offsetValue;
    }
    
    public Instant getProcessedAt() {
        return processedAt;
    }
}
