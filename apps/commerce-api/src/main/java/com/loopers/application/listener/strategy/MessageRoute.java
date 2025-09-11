package com.loopers.application.listener.strategy;

import lombok.Value;

@Value
public class MessageRoute {
    String stream;
    String key;
    
    public static MessageRoute of(String stream, String key) {
        return new MessageRoute(stream, key);
    }
}
