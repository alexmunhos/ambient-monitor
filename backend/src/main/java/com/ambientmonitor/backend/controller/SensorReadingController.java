package com.ambientmonitor.backend.controller;

import com.ambientmonitor.backend.model.SensorReading;
import com.ambientmonitor.backend.service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SensorReadingController {

    private final SensorReadingService service;

    @GetMapping("/api/readings")
    public List<SensorReading> getReadings(@RequestParam String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return service.getHistory(deviceId, from, to);
    }
}