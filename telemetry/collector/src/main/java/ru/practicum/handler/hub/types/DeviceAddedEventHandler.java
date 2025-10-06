package ru.practicum.handler.hub.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.hub.HubEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final String hubEventsTopic = "telemetry.hubs.v1";

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        var added = event.getDeviceAdded();
        System.out.printf("[Hub] DeviceAdded: id=%s, type=%s, hub=%s%n",
                added.getId(), added.getType(), event.getHubId());

        kafkaProducer.send(
                hubEventsTopic,
                event.getHubId(),
                Instant.now(),
                event
        );
    }
}
