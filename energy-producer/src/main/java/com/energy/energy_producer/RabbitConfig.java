package com.energy.energy_producer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ENERGY_MESSAGES_QUEUE = "energy.messages";

    @Bean
    public Queue energyMessagesQueue() {
        return new Queue(ENERGY_MESSAGES_QUEUE, true);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
