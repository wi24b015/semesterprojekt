package com.example.energyapi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, String> {

    List<EnergyUsage> findByHourBetweenOrderByHourAsc(String start, String end);
}