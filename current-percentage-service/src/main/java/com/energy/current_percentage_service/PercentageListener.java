package com.energy.current_percentage_service;

import com.energy.current_percentage_service.dto.EnergyUpdateDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    private final CurrentPercentageRepository percentageRepository;

    public PercentageListener(CurrentPercentageRepository percentageRepository) {
        this.percentageRepository = percentageRepository;
    }

    @RabbitListener(queues = RabbitConfig.ENERGY_UPDATES_QUEUE)
    public void handleUpdateMessage(EnergyUpdateDto update) {
        try {
            double communityProduced = update.getCommunityProduced();
            double communityUsed = update.getCommunityUsed();
            double gridUsed = update.getGridUsed();

            double communityDepleted = 0.0;
            if (communityProduced > 0) {
                communityDepleted = (communityUsed / communityProduced) * 100;
            }

            if (communityDepleted > 100) {
                communityDepleted = 100;
            }

            double totalUsed = communityUsed + gridUsed;

            double gridPortion = 0.0;
            if (totalUsed > 0) {
                gridPortion = (gridUsed / totalUsed) * 100;
            }

            CurrentPercentage currentPercentage = new CurrentPercentage(
                    update.getHour(),
                    communityDepleted,
                    gridPortion
            );

            percentageRepository.deleteAll();
            percentageRepository.save(currentPercentage);

            System.out.println("Updated current percentage for hour " + update.getHour());

        } catch (Exception e) {
            System.err.println("Error calculating current percentage: " + e.getMessage());
            e.printStackTrace();
        }
    }
}