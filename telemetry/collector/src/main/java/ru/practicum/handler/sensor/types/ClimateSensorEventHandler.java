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

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var climateData = event.getClimateSensorEvent();

        System.out.printf("[Sensor] Climate event. hub=%s, sensor=%s, temp=%s°C, humidity=%s%%, co2=%s%n",
                event.getHubId(),
                event.getId(),
                climateData.getTemperatureC(),
                climateData.getHumidity(),
                climateData.getCo2Level());

        var avroEvent = protoMapper.toAvro(event);

        String sensorEventsTopic = "telemetry.sensors.v1";
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent);

        System.out.println("[DEBUG] Climate event sent to Kafka: " + sensorEventsTopic);
    }
}