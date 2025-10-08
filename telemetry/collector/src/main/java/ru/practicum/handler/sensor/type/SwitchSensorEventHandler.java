package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        // ЖЕСТКАЯ ПРОВЕРКА: только Switch события
        if (event.getPayloadCase() != SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT) {
            log.error("🚨 CRITICAL ERROR: SwitchSensorEventHandler получил НЕПРАВИЛЬНЫЙ тип: {}. Ожидался: SWITCH_SENSOR_EVENT",
                    event.getPayloadCase());
            return;
        }

        var switchEvent = event.getSwitchSensorEvent();
        log.info("✅ SwitchSensorEventHandler: Обработка Switch события - hub={}, state={}",
                event.getHubId(), switchEvent.getState());

        var avroEvent = protoMapper.toAvro(event);

        // Проверка типа payload
        Object payload = avroEvent.getPayload();
        if (!(payload instanceof SwitchSensorAvro)) {
            log.error("🚨 CRITICAL ERROR: После маппинга ожидался SwitchSensorAvro, но получен: {}",
                    payload != null ? payload.getClass().getSimpleName() : "NULL");
            return;
        }

        String sensorEventsTopic = "telemetry.sensors.v1";
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.info("✅ Switch событие отправлено в Kafka");
    }
}