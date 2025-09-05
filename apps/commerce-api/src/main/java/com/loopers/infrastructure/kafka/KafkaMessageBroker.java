package com.loopers.infrastructure.kafka;

import com.loopers.domain.common.messaging.MessageBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageBroker implements MessageBroker {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(String destination, String key, Object message) {
        String topic = mapDestinationToTopic(destination);
        
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, message);
        
        try {
            kafkaTemplate.send(record).get(5, TimeUnit.SECONDS); // 5초 타임아웃
            
            log.debug("Message sent to Kafka topic: topic={}, key={}, messageType={}", 
                     topic, key, message.getClass().getSimpleName());
                     
        } catch (Exception e) {
            log.error("Failed to send message to Kafka: topic={}, key={}, error={}", 
                     topic, key, e.getMessage(), e);
            throw new MessagePublishException("Kafka publish failed", e);
        }
    }
    
    private String mapDestinationToTopic(String destination) {
        return destination;
    }
    
    public static class MessagePublishException extends RuntimeException {
        public MessagePublishException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
