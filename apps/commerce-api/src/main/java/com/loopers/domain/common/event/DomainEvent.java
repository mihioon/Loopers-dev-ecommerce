package com.loopers.domain.common.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String domainId;
    private final String eventType;
    private final Long version;

    protected DomainEvent(String domainId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.domainId = Objects.requireNonNull(domainId, "domainId null 오류");
        this.eventType = this.getClass().getSimpleName();
        this.version = 1L;
    }

    protected DomainEvent(String eventId, LocalDateTime occurredOn, String domainId, String eventType, Long version) {
        this.eventId = Objects.requireNonNull(eventId, "eventId null 오류");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn null 오류");
        this.domainId = Objects.requireNonNull(domainId, "domainId null 오류");
        this.eventType = Objects.requireNonNull(eventType, "eventType null 오류");
        this.version = Objects.requireNonNull(version, "version null 오류");
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getEventType() {
        return eventType;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainEvent that)) return false;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return String.format("%s{eventId='%s', domainId='%s', occurredOn=%s}", 
                eventType, eventId, domainId, occurredOn);
    }
}
