package ru.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var climateData = event.getClimateSensorEvent();

        var correctEvent = SensorEventProto.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setClimateSensorEvent(ClimateSensorProto.newBuilder()
                        .setTemperatureC(climateData.getTemperatureC())
                        .setHumidity(climateData.getHumidity())
                        .setCo2Level(climateData.getCo2Level())
                        .build())
                .build();

        System.out.printf("[Sensor] Climate event. hub=%s, temp=%s°C, humidity=%s%%, co2=%s%n",
                correctEvent.getHubId(),
                climateData.getTemperatureC(),
                climateData.getHumidity(),
                climateData.getCo2Level());

        var avroEvent = protoMapper.toAvro(correctEvent);
        String sensorEventsTopic = "telemetry.sensors.v1";
        kafkaProducer.send(sensorEventsTopic, correctEvent.getHubId(), avroEvent);
    }
}