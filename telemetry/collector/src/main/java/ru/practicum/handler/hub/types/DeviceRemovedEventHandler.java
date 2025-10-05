package ru.practicum.handler.hub.types;

import org.springframework.stereotype.Component;
import ru.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class DeviceRemovedEventHandler implements HubEventHandler {

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        System.out.println("Device removed: id=" + event.getDeviceRemoved().getId());
    }
}
