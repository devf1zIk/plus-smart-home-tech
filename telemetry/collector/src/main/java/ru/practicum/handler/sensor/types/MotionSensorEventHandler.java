package ru.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var motion = event.getMotionSensorEvent();
        System.out.printf("[Sensor] Motion event. hub=%s, motion=%s, linkQuality=%d, voltage=%d%n",
                event.getHubId(), motion.getMotion(), motion.getLinkQuality(), motion.getVoltage());

        kafkaProducer.send(
                sensorEventsTopic,
                event.getHubId(),
                Instant.now(),
                event
        );
    }
}
