package com.loopers.interfaces.consumer;

import com.loopers.events.common.DomainEvent;
import com.loopers.domain.common.messaging.IdempotencyService;
import com.loopers.domain.eventlog.EventLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogConsumer {
    
    private final EventLogService eventLogService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
        topics = {"product-view-events", "product-state-events", "like-events", "order-events", "payment-events"},
        containerFactory = "kafkaListenerContainerFactory",
        groupId = ConsumerGroupConstants.AUDIT_LOG
    )
    @Transactional
    public void handleEvent(ConsumerRecord<String, DomainEvent> record, Acknowledgment ack) {
        DomainEvent event = record.value();
        
        log.debug("AuditLogListener received event: topic={}, key={}, eventId={}", 
                 record.topic(), record.key(), event.getEventId());

        // 멱등성 체크
        if (idempotencyService.isProcessed(event.getEventId(), ConsumerGroupConstants.AUDIT_LOG)) {
            log.debug("Event already processed by AuditLogConsumer: eventId={}", event.getEventId());
            ack.acknowledge();
            return;
        }

        try {
            // 감사 로그 저장
            eventLogService.saveEventLog(event, record.topic(), record.partition(), record.offset());
            
            // 처리 완료 기록
            idempotencyService.markAsProcessed(event.getEventId(), ConsumerGroupConstants.AUDIT_LOG);
            
            log.debug("Event processed by AuditLogConsumer: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process event in AuditLogListener: eventId={}", event.getEventId(), e);
            throw e; // 재시도를 위해 예외를 다시 던짐
        }

        ack.acknowledge();
    }

}
