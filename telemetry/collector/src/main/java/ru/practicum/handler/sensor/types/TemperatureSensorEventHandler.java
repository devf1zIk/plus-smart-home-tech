package ru.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final ProtoMapper protoMapper;
    private final String sensorEventsTopic = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var temp = event.getTemperatureSensorEvent();
        System.out.printf("[Sensor] Temperature event. hub=%s, temp=%d°C/%d°F%n",
                event.getHubId(), temp.getTemperatureC(), temp.getTemperatureF());

        var avroEvent = protoMapper.toAvro(event);
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent);
    }
}