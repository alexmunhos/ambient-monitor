package com.ambientmonitor.backend.service;

import com.ambientmonitor.backend.model.SensorReading;
import com.ambientmonitor.backend.repository.InfluxDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorReadingService {

    private final InfluxDbRepository repository;

    public void save(SensorReading reading) {
        repository.save(reading);
    }

    public List<SensorReading> getHistory(String deviceId, Instant from, Instant to) {
        return repository.findByDeviceAndPeriod(deviceId, from, to);
    }
}