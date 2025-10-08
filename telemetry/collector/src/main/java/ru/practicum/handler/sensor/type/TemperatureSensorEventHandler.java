package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    String sensorEventsTopic = "telemetry.sensors.v1";


    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        // ЖЕСТКАЯ ПРОВЕРКА: только Temperature события
        if (event.getPayloadCase() != SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT) {
            log.error("🚨 CRITICAL ERROR: TemperatureSensorEventHandler получил НЕПРАВИЛЬНЫЙ тип: {}. Ожидался: TEMPERATURE_SENSOR_EVENT",
                    event.getPayloadCase());
            return;
        }

        var temp = event.getTemperatureSensorEvent();
        log.info("✅ TemperatureSensorEventHandler: Обработка Temperature события - hub={}, temp={}°C/{}°F",
                event.getHubId(), temp.getTemperatureC(), temp.getTemperatureF());

        var avroEvent = protoMapper.toAvro(event);

        // Проверка типа payload
        Object payload = avroEvent.getPayload();
        if (!(payload instanceof TemperatureSensorAvro)) {
            log.error("🚨 CRITICAL ERROR: После маппинга ожидался TemperatureSensorAvro, но получен: {}",
                    payload != null ? payload.getClass().getSimpleName() : "NULL");
            return;
        }

        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.info("✅ Temperature событие отправлено в Kafka");
    }
}