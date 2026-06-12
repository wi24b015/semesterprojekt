package com.example.energyapi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, String> {

    Optional<CurrentPercentage> findTopByOrderByHourDesc();
}