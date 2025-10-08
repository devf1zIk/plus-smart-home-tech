package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var light = event.getLightSensorEvent();
        System.out.printf("[Sensor] Light event. hub=%s, luminosity=%s, linkQuality=%s%n",
                event.getHubId(), light.getLuminosity(), light.getLinkQuality());

        var avroEvent = protoMapper.toAvro(event);
        String sensorEventsTopic = "telemetry.sensors.v1";
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(),avroEvent);
    }
}
