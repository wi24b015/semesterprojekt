package com.energy.current_percentage_service;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ENERGY_UPDATES_QUEUE = "energy.updates";

    @Bean
    public Queue energyUpdatesQueue() {
        return new Queue(ENERGY_UPDATES_QUEUE, true);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
