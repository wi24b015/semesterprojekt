package com.example.energyapi;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EnergyMessageListener {

    private final EnergyService energyService;

    public EnergyMessageListener(EnergyService energyService) {
        this.energyService = energyService;
    }

    @RabbitListener(queues = RabbitConfig.ENERGY_MESSAGES_QUEUE)
    public void receive(EnergyMessage message) {
        energyService.saveMessage(message);
    }
}
