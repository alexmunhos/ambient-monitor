package com.ambientmonitor.backend.mqtt;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MqttListener {

    private static final Logger log = LoggerFactory.getLogger(MqttListener.class);

    private final MqttConfig mqttConfig;

    public MqttListener(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

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
                log.info("Mensagem recebida no tópico [{}]: {}", topic, payload);
            });

            log.info("Inscrito no tópico: {}", mqttConfig.getTopic());

        } catch (MqttException e) {
            log.error("Erro ao conectar no broker MQTT", e);
        }
    }
}