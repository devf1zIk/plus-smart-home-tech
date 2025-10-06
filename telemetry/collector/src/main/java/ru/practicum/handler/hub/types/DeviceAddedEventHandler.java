package ru.practicum.handler.hub.types;

import org.springframework.stereotype.Component;
import ru.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class DeviceAddedEventHandler implements HubEventHandler {

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        var added = event.getDeviceAdded();
        System.out.printf("[Hub] DeviceAdded: id=%s, type=%s, hub=%s%n",
                added.getId(), added.getType(), event.getHubId());
    }
}
