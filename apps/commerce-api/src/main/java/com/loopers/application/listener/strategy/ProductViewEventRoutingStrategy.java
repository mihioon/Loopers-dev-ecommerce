package com.loopers.application.listener.strategy;

import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.product.event.ProductViewedEvent;
import org.springframework.stereotype.Component;

@Component
public class ProductViewEventRoutingStrategy implements EventRoutingStrategy {
    
    @Override
    public boolean supports(Class<? extends DomainEvent> eventClass) {
        return ProductViewedEvent.class.isAssignableFrom(eventClass);
    }
    
    @Override
    public MessageRoute route(DomainEvent event) {
        ProductViewedEvent viewEvent = (ProductViewedEvent) event;
        String stream = "product-view-events";
        String key = "product:" + viewEvent.getProductId();
        return MessageRoute.of(stream, key);
    }
}
