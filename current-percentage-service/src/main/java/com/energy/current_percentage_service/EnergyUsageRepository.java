package com.energy.current_percentage_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, String> {
}
