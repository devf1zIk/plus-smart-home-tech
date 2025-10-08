package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        // ЖЕСТКАЯ ПРОВЕРКА: только Motion события
        if (event.getPayloadCase() != SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT) {
            log.error("🚨 CRITICAL ERROR: MotionSensorEventHandler получил НЕПРАВИЛЬНЫЙ тип: {}. Ожидался: MOTION_SENSOR_EVENT",
                    event.getPayloadCase());
            return;
        }

        var motion = event.getMotionSensorEvent();
        log.info("✅ MotionSensorEventHandler: Обработка Motion события - hub={}, motion={}, linkQuality={}, voltage={}",
                event.getHubId(), motion.getMotion(), motion.getLinkQuality(), motion.getVoltage());

        var avroEvent = protoMapper.toAvro(event);

        // Проверка типа payload
        Object payload = avroEvent.getPayload();
        if (!(payload instanceof MotionSensorAvro)) {
            log.error("🚨 CRITICAL ERROR: После маппинга ожидался MotionSensorAvro, но получен: {}",
                    payload != null ? payload.getClass().getSimpleName() : "NULL");
            return;
        }

        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.info("✅ Motion событие отправлено в Kafka");
    }
}