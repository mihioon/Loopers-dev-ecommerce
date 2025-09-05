package com.loopers.infrastructure.kafka.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {
    
    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
    
    @Modifying
    @Query("DELETE FROM ProcessedEvent e WHERE e.handledAt < :cutoffTime")
    void deleteOldRecords(@Param("cutoffTime") Instant cutoffTime);
}
