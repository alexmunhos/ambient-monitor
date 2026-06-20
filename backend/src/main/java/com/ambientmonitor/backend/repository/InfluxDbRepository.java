package com.ambientmonitor.backend.repository;

import com.ambientmonitor.backend.model.SensorReading;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class InfluxDbRepository {

    private static final Logger log = LoggerFactory.getLogger(InfluxDbRepository.class);

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    public InfluxDbRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void save(SensorReading reading) {
        Point point = Point.measurement("sensor_readings")
                .addTag("device_id", reading.getDeviceId())
                .addTag("location", reading.getLocation())
                .addField("temperature", reading.getTemperature())
                .addField("humidity", reading.getHumidity())
                .time(reading.getReceivedAt(), WritePrecision.MS);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writePoint(bucket, org, point);

        log.info("Gravado no InfluxDB: device={}, temp={}, humidity={}",
                reading.getDeviceId(), reading.getTemperature(), reading.getHumidity());
    }
}