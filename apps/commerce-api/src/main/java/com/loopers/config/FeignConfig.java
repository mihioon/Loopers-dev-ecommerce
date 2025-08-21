package com.loopers.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients(basePackages = "com.loopers.infrastructure.external")
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5000, TimeUnit.MILLISECONDS,  // connection timeout
                10000, TimeUnit.MILLISECONDS, // read timeout
                true // follow redirects
        );
    }
    
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                100,  // period
                1000, // maxPeriod
                3     // maxAttempts
        );
    }
}
