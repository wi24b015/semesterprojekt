package com.example.energyapi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnergyMeasurementRepository extends JpaRepository<EnergyMeasurement, Long> {

    Optional<EnergyMeasurement> findTopByOrderByDatetimeDesc();

    List<EnergyMeasurement> findByDatetimeBetweenOrderByDatetimeAsc(LocalDateTime start, LocalDateTime end);
}
