package com.ambientmonitor.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {
    private String deviceId;
    private String location;
    private double temperature;
    private double humidity;
    private Instant receivedAt;
}