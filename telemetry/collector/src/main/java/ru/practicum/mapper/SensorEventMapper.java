package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.event.sensor.base.SensorEvent;
import ru.practicum.event.sensor.types.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@UtilityClass
public class SensorEventMapper {

    public static SensorEventAvro SensorEventAvro(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(toSensorEventAvroPay(sensorEvent))
                .build();
    }

    public static SpecificRecordBase toSensorEventAvroPay(SensorEvent sensorEvent) {
        switch (sensorEvent.getType()) {
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent e = (MotionSensorEvent) sensorEvent;
                return MotionSensorAvro.newBuilder()
                        .setLinkQuality(nz(e.getLinkQuality()))
                        .setMotion(nb(e.getMotion()))
                        .setVoltage(nz(e.getVoltage()))
                        .build();
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent e = (ClimateSensorEvent) sensorEvent;
                return ClimateSensorAvro.newBuilder()
                        .setTemperatureC(nz(e.getTemperatureC()))
                        .setHumidity(nz(e.getHumidity()))
                        .setCo2Level(nz(e.getCo2Level()))
                        .build();
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent e = (LightSensorEvent) sensorEvent;
                return LightSensorAvro.newBuilder()
                        .setLinkQuality(nz(e.getLinkQuality()))
                        .setLuminosity(nz(e.getLuminosity()))
                        .build();
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent e = (SwitchSensorEvent) sensorEvent;
                return SwitchSensorAvro.newBuilder()
                        .setState(nb(e.getState()))
                        .build();
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent e = (TemperatureSensorEvent) sensorEvent;
                return TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(nz(e.getTemperatureC()))
                        .setTemperatureF(nz(e.getTemperatureF()))
                        .build();
            }
            default -> throw new IllegalStateException("Error payload: " + sensorEvent.getType());
        }
    }

    private static int nz(Integer v) {
        return v != null ? v : 0;
    }

    private static boolean nb(Boolean v) {
        return v != null && v;
    }
}
