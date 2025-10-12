package ru.practicum.handler.sensor.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.protoMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {
    private final KafkaEventProducer kafkaProducer;
    private final protoMapper protoMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var switchEvent = event.getSwitchSensorEvent();
        System.out.printf("[Sensor] Switch event. hub=%s, state=%s%n",
                event.getHubId(), switchEvent.getState());

        var avroEvent = protoMapper.toAvro(event);
        String sensorEventsTopic = "telemetry.sensors.v1";
        kafkaProducer.send(sensorEventsTopic, event.getHubId(), avroEvent.getTimestamp(), avroEvent);
    }
}