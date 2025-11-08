package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface EventService {

    void publishSensorEvent(SensorEventProto event);

    void publishHubEvent(HubEventProto event);

}
