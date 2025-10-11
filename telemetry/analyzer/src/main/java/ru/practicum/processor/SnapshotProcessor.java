package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.service.ScenarioExecutionService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final ScenarioExecutionService scenarioExecutionService;

    @KafkaListener(topics = "${analyzer.topics.snapshots}", groupId = "analyzer-snapshots-group")
    public void listenSnapshots(SensorsSnapshotAvro snapshot) {
        log.debug("Received snapshot: {}", snapshot);
        scenarioExecutionService.processSnapshot(snapshot);
    }
}