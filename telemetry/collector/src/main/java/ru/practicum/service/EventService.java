package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.KafkaConfigProperties;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.ProtoAvroHubMapper;
import ru.practicum.mapper.ProtoAvroSensorMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final KafkaConfigProperties kafkaProperties;
    private final KafkaEventProducer kafkaEventProducer;
    private final ProtoAvroHubMapper protoAvroHubMapper;
    private final ProtoAvroSensorMapper protoAvroSensorMapper;

    public void publishSensorEvent(SensorEventProto proto) {
        String topic = kafkaProperties.getSensorEventsTopic();
        String key = proto.getHubId();

        var avroEvent = protoAvroSensorMapper.toAvro(proto);

        log.info("Publish sensor event | topic={} | key={} | sensorId={} | ts={}",
                topic, key, proto.getId(), avroEvent.getTimestamp());

        kafkaEventProducer.send(topic, key, avroEvent.getTimestamp(), avroEvent);
    }

    public void publishHubEvent(HubEventProto proto) {
        String topic = kafkaProperties.getHubEventsTopic();
        String key = proto.getHubId();

        var avroEvent = protoAvroHubMapper.toAvro(proto);

        log.info("Publish hub event | topic={} | key={} | ts={}",
                topic, key, avroEvent.getTimestamp());

        kafkaEventProducer.send(topic, key, avroEvent.getTimestamp(), avroEvent);
    }
}
