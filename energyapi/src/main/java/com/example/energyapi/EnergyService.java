package com.example.energyapi;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EnergyService {

    private static final String PRODUCER = "PRODUCER";
    private static final String USER = "USER";

    private final EnergyMeasurementRepository repository;

    public EnergyService(EnergyMeasurementRepository repository) {
        this.repository = repository;
    }

    public CurrentEnergy getCurrentEnergy() {
        return repository.findTopByOrderByDatetimeDesc()
                .map(latest -> latest.getDatetime().truncatedTo(ChronoUnit.HOURS))
                .map(hour -> aggregateCurrentHour(hour, repository.findByDatetimeBetweenOrderByDatetimeAsc(hour, hour.plusHours(1))))
                .orElseGet(() -> new CurrentEnergy(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).toString(), 0.0, 0.0));
    }

    public List<HistoricalEnergy> getHistoricalEnergy(LocalDateTime start, LocalDateTime end) {
        Map<LocalDateTime, EnergyTotals> totalsByHour = new TreeMap<>();

        for (EnergyMeasurement measurement : repository.findByDatetimeBetweenOrderByDatetimeAsc(start, end)) {
            LocalDateTime hour = measurement.getDatetime().truncatedTo(ChronoUnit.HOURS);
            totalsByHour.computeIfAbsent(hour, ignored -> new EnergyTotals()).add(measurement);
        }

        return totalsByHour.entrySet().stream()
                .map(entry -> entry.getValue().toHistoricalEnergy(entry.getKey()))
                .sorted(Comparator.comparing(energy -> energy.hour))
                .toList();
    }

    public void saveMessage(EnergyMessage message) {
        repository.save(new EnergyMeasurement(
                message.getType(),
                message.getAssociation(),
                message.getKwh(),
                LocalDateTime.parse(message.getDatetime())
        ));
    }

    private CurrentEnergy aggregateCurrentHour(LocalDateTime hour, List<EnergyMeasurement> measurements) {
        EnergyTotals totals = new EnergyTotals();
        measurements.forEach(totals::add);

        return new CurrentEnergy(
                hour.toString(),
                Math.min(totals.produced, totals.used),
                Math.max(totals.used - totals.produced, 0.0)
        );
    }

    private static class EnergyTotals {
        private double produced;
        private double used;

        void add(EnergyMeasurement measurement) {
            if (PRODUCER.equalsIgnoreCase(measurement.getType())) {
                produced += measurement.getKwh();
            } else if (USER.equalsIgnoreCase(measurement.getType())) {
                used += measurement.getKwh();
            }
        }

        HistoricalEnergy toHistoricalEnergy(LocalDateTime hour) {
            return new HistoricalEnergy(
                    hour.toString(),
                    produced,
                    used,
                    Math.max(used - produced, 0.0)
            );
        }
    }
}
