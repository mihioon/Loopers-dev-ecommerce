package com.loopers.domain.eventlog;

import java.time.Instant;
import java.util.List;

public interface EventLogRepository {
    
    EventLog save(EventLog eventLog);
    
    boolean existsByEventId(String eventId);
    
    List<EventLog> findByEventTypeAndTimeRange(String eventType, Instant startTime, Instant endTime);
    
    List<EventLog> findByAggregateId(String aggregateId);
}
