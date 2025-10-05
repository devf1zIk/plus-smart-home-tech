package ru.practicum.handler.sensor.types;

import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class LightSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Light sensor event: Luminosity=" + event.getLightSensorEvent().getLuminosity());
    }
}
