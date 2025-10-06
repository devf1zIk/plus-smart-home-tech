package ru.practicum.handler.hub.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.handler.hub.HubEventHandler;
import ru.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final String hubEventsTopic = "telemetry.hubs.v1";

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Device removed: id=" + event.getDeviceRemoved().getId());

        kafkaProducer.send(
                hubEventsTopic,
                event.getHubId(),
                Instant.now(),
                event
        );
    }
}
