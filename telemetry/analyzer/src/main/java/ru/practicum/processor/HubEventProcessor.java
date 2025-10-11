package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor {

    private final HubEventService hubEventService;

    @KafkaListener(topics = "${analyzer.topics.hub-events}", groupId = "analyzer-hub-events-group")
    public void listenHubEvents(HubEventAvro event) {
        log.debug("Received hub event: {}", event);

        switch (event.getPayload().getClass().getSimpleName()) {
            case "DeviceAddedEventAvro" -> hubEventService.handleDeviceAdded(event);
            case "DeviceRemovedEventAvro" -> hubEventService.handleDeviceRemoved(event);
            case "ScenarioAddedEventAvro" -> hubEventService.handleScenarioAdded(event);
            case "ScenarioRemovedEventAvro" -> hubEventService.handleScenarioRemoved(event);
            default -> log.warn("Неизвестный тип события: {}", event.getPayload().getClass());
        }
    }
}