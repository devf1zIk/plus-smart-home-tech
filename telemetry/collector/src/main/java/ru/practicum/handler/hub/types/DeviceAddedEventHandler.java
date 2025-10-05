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
        HubEventProto.DeviceAddedEventProto added = event.getDeviceAdded();
        System.out.println("Device added: id=" + added.getId() + ", type=" + added.getType());
    }
}
