package com.loopers.domain.common.messaging;

public interface MessageBroker {

    void publish(String destination, String key, Object message);

    default void publish(String destination, Object message) {
        publish(destination, null, message);
    }
}
