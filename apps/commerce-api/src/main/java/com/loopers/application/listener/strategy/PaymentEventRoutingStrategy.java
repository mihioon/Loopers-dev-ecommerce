package com.loopers.application.listener.strategy;

import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.payment.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventRoutingStrategy implements EventRoutingStrategy {
    
    @Override
    public boolean supports(Class<? extends DomainEvent> eventClass) {
        return PaymentEvent.class.isAssignableFrom(eventClass);
    }
    
    @Override
    public MessageRoute route(DomainEvent event) {
        PaymentEvent paymentEvent = (PaymentEvent) event;
        String stream = "payment-events";
        String key = "payment:" + paymentEvent.getDomainId();
        return MessageRoute.of(stream, key);
    }
}
