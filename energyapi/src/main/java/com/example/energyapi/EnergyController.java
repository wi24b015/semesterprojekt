package com.example.energyapi;

import com.example.energyapi.dto.CurrentEnergyDto;
import com.example.energyapi.dto.HistoricalEnergyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/energy/current")
    public ResponseEntity<?> getCurrentEnergy() {
        CurrentEnergyDto currentEnergy = energyService.getCurrentEnergy();

        if (currentEnergy == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse(404, "Not Found", "No current energy data available."));
        }

        return ResponseEntity.ok(currentEnergy);
    }

    @GetMapping("/energy/historical")
    public ResponseEntity<?> getHistoricalEnergy(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDate = parseStart(start);
        LocalDateTime endDate = parseEnd(end);

        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            return ResponseEntity
                    .badRequest()
                    .body(errorResponse(400, "Bad Request", "End date must be after start date."));
        }

        List<HistoricalEnergyDto> historicalEnergy = energyService.getHistoricalEnergy(startDate, endDate);

        if (historicalEnergy.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse(404, "Not Found", "No historical energy data found for this period."));
        }

        return ResponseEntity.ok(historicalEnergy);
    }

    private LocalDateTime parseStart(String value) {
        return parseDateTimeOrDate(value);
    }

    private LocalDateTime parseEnd(String value) {
        if (value.contains("T")) {
            return LocalDateTime.parse(value);
        }

        return LocalDate.parse(value).plusDays(1).atStartOfDay();
    }

    private LocalDateTime parseDateTimeOrDate(String value) {
        if (value.contains("T")) {
            return LocalDateTime.parse(value);
        }

        return LocalDate.parse(value).atStartOfDay();
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateParseException() {
        return ResponseEntity
                .badRequest()
                .body(errorResponse(
                        400,
                        "Bad Request",
                        "Invalid date format. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss."
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(
            MissingServletRequestParameterException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(errorResponse(
                        400,
                        "Bad Request",
                        "Missing required parameter: " + exception.getParameterName()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse(
                        500,
                        "Internal Server Error",
                        "An unexpected error occurred."
                ));
    }

    private Map<String, Object> errorResponse(int status, String error, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}