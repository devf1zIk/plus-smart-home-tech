package ru.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    private final String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var climate = event.getClimateSensorEvent();
        System.out.printf("[Sensor] Climate event. hub=%s, temp=%d°C, humidity=%d%%, co2=%d%n",
                event.getHubId(), climate.getTemperatureC(), climate.getHumidity(), climate.getCo2Level());

        var avroEvent = protoMapper.toAvro(event);
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent);
    }
}