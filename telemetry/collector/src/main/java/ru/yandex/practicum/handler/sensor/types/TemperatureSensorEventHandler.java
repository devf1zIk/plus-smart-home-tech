package ru.yandex.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.mapper.protoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {
    private final KafkaEventProducer kafkaProducer;
    private final protoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var temp = event.getTemperatureSensorEvent();
        System.out.printf("[Sensor] Temperature event. hub=%s, temp=%s°C/%s°F%n",
                event.getHubId(), temp.getTemperatureC(), temp.getTemperatureF());

        var avroEvent = protoMapper.toAvro(event);
        String sensorEventsTopic = "telemetry.sensors.v1";
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent.getTimestamp(),avroEvent);
    }
}