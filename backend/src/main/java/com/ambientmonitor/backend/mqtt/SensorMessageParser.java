package com.ambientmonitor.backend.mqtt;

import com.ambientmonitor.backend.model.SensorReading;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SensorMessageParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SensorReading parse(String payload) throws Exception {
        JsonNode node = objectMapper.readTree(payload);

        return new SensorReading(
                node.get("device_id").asText(),
                node.has("location") ? node.get("location").asText() : "unknown",
                node.get("temperature").asDouble(),
                node.get("humidity").asDouble(),
                Instant.now()
        );
    }
}