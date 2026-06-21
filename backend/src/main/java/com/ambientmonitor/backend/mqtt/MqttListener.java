package com.ambientmonitor.backend.mqtt;

import com.ambientmonitor.backend.model.SensorReading;
import com.ambientmonitor.backend.service.SensorReadingService;
import com.ambientmonitor.backend.websocket.SensorWebSocketHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttListener {

    private static final Logger log = LoggerFactory.getLogger(MqttListener.class);

    private final MqttConfig mqttConfig;
    private final SensorMessageParser parser;
    private final SensorReadingService service;
    private final SensorWebSocketHandler webSocketHandler;

    @PostConstruct
    public void connectAndSubscribe() {
        try {
            MqttClient client = new MqttClient(
                    mqttConfig.getBrokerUrl(),
                    mqttConfig.getClientId(),
                    new MemoryPersistence()
            );

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.connect(options);
            log.info("Conectado ao broker MQTT: {}", mqttConfig.getBrokerUrl());

            client.subscribe(mqttConfig.getTopic(), (topic, message) -> {
                String payload = new String(message.getPayload());
                try {
                    SensorReading reading = parser.parse(payload);
                    service.save(reading);
                    webSocketHandler.broadcast(reading);
                } catch (Exception e) {
                    log.error("Erro ao processar mensagem: {}", payload, e);
                }
            });

            log.info("Inscrito no tópico: {}", mqttConfig.getTopic());

        } catch (MqttException e) {
            log.error("Erro ao conectar no broker MQTT", e);
        }
    }
}