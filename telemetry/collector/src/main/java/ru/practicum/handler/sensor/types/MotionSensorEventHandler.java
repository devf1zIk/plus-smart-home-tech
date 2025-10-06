package ru.practicum.handler.sensor.types;

import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class MotionSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var motion = event.getMotionSensorEvent();
        System.out.printf("[Sensor] Motion event. hub=%s, motion=%s, linkQuality=%d, voltage=%d%n",
                event.getHubId(), motion.getMotion(), motion.getLinkQuality(), motion.getVoltage());
    }
}
