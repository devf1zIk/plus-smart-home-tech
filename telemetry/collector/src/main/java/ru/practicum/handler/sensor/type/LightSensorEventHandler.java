package ru.practicum.handler.sensor.type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        // ЖЕСТКАЯ ПРОВЕРКА: только Light события
        if (event.getPayloadCase() != SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT) {
            log.error("🚨 CRITICAL ERROR: LightSensorEventHandler получил НЕПРАВИЛЬНЫЙ тип: {}. Ожидался: LIGHT_SENSOR_EVENT",
                    event.getPayloadCase());
            return;
        }

        if (!event.hasLightSensorEvent()) {
            log.error("🚨 CRITICAL ERROR: LightSensorEventHandler - отсутствуют light данные");
            return;
        }

        var light = event.getLightSensorEvent();

        log.info("✅ LightSensorEventHandler: Обработка Light события - hubId={}, luminosity={}, linkQuality={}",
                event.getHubId(), light.getLuminosity(), light.getLinkQuality());

        var avroEvent = protoMapper.toAvro(event);

        // Проверка типа payload
        Object payload = avroEvent.getPayload();
        if (!(payload instanceof LightSensorAvro)) {
            log.error("🚨 CRITICAL ERROR: После маппинга ожидался LightSensorAvro, но получен: {}",
                    payload != null ? payload.getClass().getSimpleName() : "NULL");
            return;
        }

        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.debug("✅ Light событие отправлено в Kafka: hubId={}, topic={}",
                event.getHubId(), sensorEventsTopic);
    }
}