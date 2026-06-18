#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include "config.h"

#define DHTPIN 4
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

WiFiClient espClient;
PubSubClient client(espClient);

void setup_wifi() {
    delay(10);
    Serial.println("Conectando ao WiFi...");
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    Serial.println("\nWiFi conectado. IP: ");
    Serial.println(WiFi.localIP());
}

void reconnect() {
    while (!client.connected()) {
        Serial.print("Conectando ao MQTT...");
        if (client.connect(DEVICE_ID)) {
            Serial.println("conectado!");
        } else {
            Serial.print("falhou, rc=");
            Serial.print(client.state());
            Serial.println(" tentando novamente em 5s");
            delay(5000);
        }
    }
}

void setup() {
    Serial.begin(115200);
    dht.begin();
    setup_wifi();
    client.setServer(MQTT_SERVER, MQTT_PORT);
}

void loop() {
    if (!client.connected()) reconnect();
    client.loop();

    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();

    if (isnan(temperature) || isnan(humidity)) {
        Serial.println("Erro ao ler DHT11!");
        return;
    }

    String payload = "{";
    payload += "\"device_id\":\"" + String(DEVICE_ID) + "\",";
    payload += "\"temperature\":" + String(temperature) + ",";
    payload += "\"humidity\":" + String(humidity) + ",";
    payload += "\"uptime_ms\":" + String(millis());
    payload += "}";

    Serial.println("Publicando: " + payload);
    client.publish(MQTT_TOPIC, payload.c_str());

    delay(5000);
}