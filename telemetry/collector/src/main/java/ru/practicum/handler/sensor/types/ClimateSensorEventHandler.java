package ru.practicum.handler.sensor.types;

import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        System.out.println("Climate sensor event: Humidity=" + event.getClimateSensorEvent().getHumidity()
                + ", Temperature=" + event.getClimateSensorEvent().getTemperatureC());
    }
}
