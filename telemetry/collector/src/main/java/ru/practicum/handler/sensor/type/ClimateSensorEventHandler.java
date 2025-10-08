package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var climateData = event.getClimateSensorEvent();

        log.info("Обработка события климатического датчика: hubId={}, sensorId={}, temp={}°C, humidity={}%, co2={}",
                event.getHubId(),
                event.getId(),
                climateData.getTemperatureC(),
                climateData.getHumidity(),
                climateData.getCo2Level());

        var avroEvent = protoMapper.toAvro(event);
        String sensorEventsTopic = "telemetry.sensors.v1";

        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.debug("Событие климатического датчика отправлено в Kafka: hubId={}, sensorId={}, topic={}",
                event.getHubId(), event.getId(), sensorEventsTopic);
    }
}