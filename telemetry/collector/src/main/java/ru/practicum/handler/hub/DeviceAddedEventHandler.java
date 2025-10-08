package ru.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.KafkaEventProducer;
import ru.practicum.mapper.protoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final protoMapper protoMapper;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        var added = event.getDeviceAdded();
        System.out.printf("[Hub] DeviceAdded: id=%s, type=%s, hub=%s%n",
                added.getId(), added.getType(), event.getHubId());

        var avroEvent = protoMapper.toAvro(event);
        if (avroEvent != null) {
            kafkaProducer.send("telemetry.hubs.v1", event.getHubId(), Instant.now(), avroEvent);
        } else {
            System.out.println("Skipping Kafka send due to null value or hubId");
        }
    }
}
