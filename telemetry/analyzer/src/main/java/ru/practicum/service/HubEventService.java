package ru.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {

    void handleDeviceAdded(HubEventAvro event);

    void handleDeviceRemoved(HubEventAvro event);

    void handleScenarioAdded(HubEventAvro event);

    void handleScenarioRemoved(HubEventAvro event);
}