package com.ambientmonitor.backend.repository;

import com.ambientmonitor.backend.model.SensorReading;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InfluxDbRepository {

    private static final Logger log = LoggerFactory.getLogger(InfluxDbRepository.class);

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    private final InfluxDBClient influxDBClient;

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

    public List<SensorReading> findByDeviceAndPeriod(String deviceId, Instant from, Instant to) {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "sensor_readings")
                  |> filter(fn: (r) => r.device_id == "%s")
                  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
                """, bucket, from.toString(), to.toString(), deviceId);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);

        List<SensorReading> readings = new ArrayList<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                SensorReading reading = new SensorReading();
                reading.setDeviceId(deviceId);
                reading.setLocation(String.valueOf(record.getValueByKey("location")));
                reading.setTemperature(parseDouble(record.getValueByKey("temperature")));
                reading.setHumidity(parseDouble(record.getValueByKey("humidity")));
                reading.setReceivedAt(record.getTime());
                readings.add(reading);
            }
        }

        return readings;
    }

    private double parseDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return 0.0;
    }
}