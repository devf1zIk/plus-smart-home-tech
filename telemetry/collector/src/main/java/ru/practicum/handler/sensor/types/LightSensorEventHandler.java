package ru.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    private final String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var light = event.getLightSensorEvent();
        System.out.printf("[Sensor] Light event. hub=%s, luminosity=%d, linkQuality=%d%n",
                event.getHubId(), light.getLuminosity(), light.getLinkQuality());

        var avroEvent = protoMapper.toAvro(event);
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent);
    }
}