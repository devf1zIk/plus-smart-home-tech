package ru.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface ScenarioExecutionService {

    void processSnapshot(SensorsSnapshotAvro snapshot);
}
