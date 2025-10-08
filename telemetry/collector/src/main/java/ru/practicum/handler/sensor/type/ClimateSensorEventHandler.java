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
    String sensorEventsTopic = "telemetry.sensors.v1";


    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("🎯 ClimateSensorEventHandler START: id={}, hub={}, payloadCase={}",
                event.getId(), event.getHubId(), event.getPayloadCase());

        // СУПЕР ЖЕСТКАЯ ПРОВЕРКА
        if (event.getPayloadCase() != SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT) {
            log.error("🚨🚨🚨 CRITICAL: Climate handler got WRONG type: {}", event.getPayloadCase());
            log.error("Event details: id={}, hubId={}", event.getId(), event.getHubId());
            return;
        }

        if (!event.hasClimateSensorEvent()) {
            log.error("🚨🚨🚨 CRITICAL: Climate handler - no climate data");
            return;
        }

        var climateData = event.getClimateSensorEvent();
        log.info("🌡️ Climate data: temp={}°C, humidity={}%, co2={}",
                climateData.getTemperatureC(), climateData.getHumidity(), climateData.getCo2Level());

        // Маппинг
        var avroEvent = protoMapper.toAvro(event);
        if (avroEvent == null) {
            log.error("🚨 ProtoMapper returned null");
            return;
        }

        // Проверка payload
        Object payload = avroEvent.getPayload();
        log.info("📦 After mapping - payload class: {}",
                payload != null ? payload.getClass().getName() : "NULL");

        if (payload != null) {
            String className = payload.getClass().getSimpleName();
            if (className.equals("LightSensorAvro")) {
                log.error("🚨🚨🚨 DISASTER: Climate event mapped to LightSensorAvro!");
                log.error("This should NEVER happen!");
            } else if (className.equals("ClimateSensorAvro")) {
                log.info("✅ SUCCESS: Correctly mapped to ClimateSensorAvro");
            } else {
                log.warn("⚠️ Unexpected payload type: {}", className);
            }
        }
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), Instant.now(), avroEvent);

        log.info("✅ Climate event SENT to Kafka");
    }
}