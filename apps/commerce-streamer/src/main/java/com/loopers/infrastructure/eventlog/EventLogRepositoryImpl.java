package com.loopers.infrastructure.eventlog;

import com.loopers.domain.eventlog.EventLog;
import com.loopers.domain.eventlog.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {
    
    private final EventLogJpaRepository eventLogJpaRepository;
    
    @Override
    public EventLog save(EventLog eventLog) {
        return eventLogJpaRepository.save(eventLog);
    }
    
    @Override
    public boolean existsByEventId(String eventId) {
        return eventLogJpaRepository.existsByEventId(eventId);
    }
    
    @Override
    public List<EventLog> findByEventTypeAndTimeRange(String eventType, Instant startTime, Instant endTime) {
        return eventLogJpaRepository.findByEventTypeAndTimeRange(eventType, startTime, endTime);
    }
    
    @Override
    public List<EventLog> findByAggregateId(String aggregateId) {
        return eventLogJpaRepository.findByAggregateId(aggregateId);
    }
}
