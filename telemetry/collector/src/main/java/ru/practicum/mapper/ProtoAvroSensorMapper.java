package ru.practicum.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;

@Component
public class ProtoAvroSensorMapper {

    public SensorEventAvro toAvro(SensorEventProto proto) {
        if (proto == null) {
            throw new IllegalArgumentException("SensorEventProto cannot be null");
        }

        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder();

        builder.setPayload(proto.getId());
        builder.setHubId(proto.getHubId());

        Instant timestamp;
        if (proto.hasTimestamp()) {
            long seconds = proto.getTimestamp().getSeconds();
            int nanos = proto.getTimestamp().getNanos();
            timestamp = Instant.ofEpochSecond(seconds, nanos);
        } else {
            timestamp = Instant.now();
        }
        builder.setTimestamp(timestamp);

        SpecificRecordBase payload = createPayload(proto);
        builder.setPayload(payload);

        return builder.build();
    }

    private SpecificRecordBase createPayload(SensorEventProto proto) {
        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        if (payloadCase == SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT) {
            return createClimateSensorPayload(proto.getClimateSensorEvent());
        }

        if (payloadCase == SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT) {
            return createLightSensorPayload(proto.getLightSensorEvent());
        }

        if (payloadCase == SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT) {
            return createMotionSensorPayload(proto.getMotionSensorEvent());
        }

        if (payloadCase == SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT) {
            return createSwitchSensorPayload(proto.getSwitchSensorEvent());
        }

        if (payloadCase == SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT) {
            return createTemperatureSensorPayload(proto.getTemperatureSensorEvent());
        }

        if (payloadCase == SensorEventProto.PayloadCase.PAYLOAD_NOT_SET) {
            throw new IllegalArgumentException("Sensor payload is empty for sensorId: " + proto.getId());
        }

        throw new IllegalStateException("Unknown sensor payload case: " + payloadCase);
    }

    private ClimateSensorAvro createClimateSensorPayload(ClimateSensorProto proto) {
        ClimateSensorAvro.Builder builder = ClimateSensorAvro.newBuilder();
        builder.setTemperatureC(proto.getTemperatureC());
        builder.setHumidity(proto.getHumidity());
        builder.setCo2Level(proto.getCo2Level());
        return builder.build();
    }

    private LightSensorAvro createLightSensorPayload(LightSensorProto proto) {
        LightSensorAvro.Builder builder = LightSensorAvro.newBuilder();
        builder.setLuminosity(proto.getLuminosity());
        builder.setLinkQuality(proto.getLinkQuality());
        return builder.build();
    }

    private MotionSensorAvro createMotionSensorPayload(MotionSensorProto proto) {
        MotionSensorAvro.Builder builder = MotionSensorAvro.newBuilder();
        builder.setMotion(proto.getMotion());
        builder.setLinkQuality(proto.getLinkQuality());
        builder.setVoltage(proto.getVoltage());
        return builder.build();
    }

    private SwitchSensorAvro createSwitchSensorPayload(SwitchSensorProto proto) {
        SwitchSensorAvro.Builder builder = SwitchSensorAvro.newBuilder();
        builder.setState(proto.getState());
        return builder.build();
    }

    private TemperatureSensorAvro createTemperatureSensorPayload(TemperatureSensorProto proto) {
        TemperatureSensorAvro.Builder builder = TemperatureSensorAvro.newBuilder();
        builder.setTemperatureC(proto.getTemperatureC());
        builder.setTemperatureF(proto.getTemperatureF());
        return builder.build();
    }
}