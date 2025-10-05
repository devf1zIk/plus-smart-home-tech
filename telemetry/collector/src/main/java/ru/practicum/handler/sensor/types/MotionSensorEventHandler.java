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
        System.out.println("Motion sensor event received: Hub=" + event.getHubId()
                + ", Device=" + event.getDeviceId()
                + ", Timestamp=" + event.getTimestamp());
    }
}
