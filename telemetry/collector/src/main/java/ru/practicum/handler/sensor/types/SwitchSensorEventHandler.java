package ru.practicum.handler.sensor.types;

import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class SwitchSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Switch sensor event: State=" + event.getSwitchSensorEvent().getState());
    }
}
