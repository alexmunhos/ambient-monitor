package com.ambientmonitor.backend.mqtt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    private String brokerUrl;
    private String clientId;
    private String topic;
}