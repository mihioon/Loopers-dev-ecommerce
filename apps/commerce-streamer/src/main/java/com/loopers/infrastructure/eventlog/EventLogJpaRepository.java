package com.loopers.infrastructure.eventlog;

import com.loopers.domain.eventlog.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface EventLogJpaRepository extends JpaRepository<EventLog, Long> {
    
    boolean existsByEventId(String eventId);
    
    @Query("SELECT e FROM EventLog e WHERE e.eventType = :eventType AND e.occurredOn BETWEEN :startTime AND :endTime ORDER BY e.occurredOn DESC")
    List<EventLog> findByEventTypeAndTimeRange(@Param("eventType") String eventType, 
                                              @Param("startTime") Instant startTime, 
                                              @Param("endTime") Instant endTime);
    
    @Query("SELECT e FROM EventLog e WHERE e.aggregateId = :aggregateId ORDER BY e.occurredOn DESC")
    List<EventLog> findByAggregateId(@Param("aggregateId") String aggregateId);
}
