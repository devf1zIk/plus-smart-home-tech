package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        // ЖЕСТКАЯ ПРОВЕРКА 1: тип события
        if (event.getPayloadCase() != SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT) {
            log.error("🚨 CRITICAL ERROR: ClimateSensorEventHandler получил НЕПРАВИЛЬНЫЙ тип: {}. Ожидался: CLIMATE_SENSOR_EVENT",
                    event.getPayloadCase());
            return;
        }

        // ЖЕСТКАЯ ПРОВЕРКА 2: наличие данных
        if (!event.hasClimateSensorEvent()) {
            log.error("🚨 CRITICAL ERROR: ClimateSensorEventHandler - отсутствуют climate данные");
            return;
        }

        var climateData = event.getClimateSensorEvent();

        log.info("✅ ClimateSensorEventHandler: Обработка Climate события - hubId={}, sensorId={}, temp={}°C, humidity={}%, co2={}",
                event.getHubId(),
                event.getId(),
                climateData.getTemperatureC(),
                climateData.getHumidity(),
                climateData.getCo2Level());

        var avroEvent = protoMapper.toAvro(event);

        // ЖЕСТКАЯ ПРОВЕРКА 3: тип payload после маппинга
        Object payload = avroEvent.getPayload();
        if (!(payload instanceof ClimateSensorAvro)) {
            log.error("🚨 CRITICAL ERROR: После маппинга ожидался ClimateSensorAvro, но получен: {}",
                    payload != null ? payload.getClass().getSimpleName() : "NULL");
            return;
        }

        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.info("✅ Climate событие успешно отправлено в Kafka: hubId={}, sensorId={}",
                event.getHubId(), event.getId());
    }
}