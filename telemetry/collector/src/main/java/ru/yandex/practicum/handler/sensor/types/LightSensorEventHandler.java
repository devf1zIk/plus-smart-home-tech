package ru.yandex.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.mapper.protoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    private final KafkaEventProducer kafkaProducer;
    private final protoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var light = event.getLightSensorEvent();

        log.info("Обработка события датчика освещенности: hubId={}, luminosity={}, linkQuality={}",
                event.getHubId(), light.getLuminosity(), light.getLinkQuality());

        var avroEvent = protoMapper.toAvro(event);
        String sensorEventsTopic = "telemetry.sensors.v1";

        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent.getTimestamp(), avroEvent);

        log.debug("Событие датчика освещенности отправлено в Kafka: hubId={}, topic={}",
                event.getHubId(), sensorEventsTopic);
    }
}