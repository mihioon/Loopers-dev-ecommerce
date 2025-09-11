package com.loopers.domain.eventlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.events.common.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventLogService {
    
    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;
    
    public void saveEventLog(DomainEvent event, String source, int partition, long offset) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            Instant occurredOn = convertToInstant(event.getOccurredOn());
            
            EventLog eventLog = new EventLog(
                event.getEventId(),
                event.getEventType(),
                occurredOn,
                event.getDomainId(),
                payload,
                source,
                partition,
                offset
            );
            
            eventLogRepository.save(eventLog);
            log.debug("Event log saved: eventId={}, eventType={}", event.getEventId(), event.getEventType());
            
        } catch (Exception e) {
            log.error("Failed to save event log: eventId={}, error={}", event.getEventId(), e.getMessage(), e);
            throw new RuntimeException("Event log save failed", e);
        }
    }
    
    private Instant convertToInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
