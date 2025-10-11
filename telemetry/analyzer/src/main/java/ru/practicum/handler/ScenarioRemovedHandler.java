package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScenarioRemovedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro hubEventAvro) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) hubEventAvro.getPayload();

        scenarioRepository.findByHubIdAndName(hubEventAvro.getHubId(), scenarioRemovedEvent.getName())
                .ifPresentOrElse(
                        scenario -> {
                            scenarioRepository.delete(scenario);
                            log.info("Scenario '{}' deleted for hub '{}'", scenarioRemovedEvent.getName(), hubEventAvro.getHubId());
                        },
                        () -> log.warn("Scenario '{}' not found for hub '{}'", scenarioRemovedEvent.getName(), hubEventAvro.getHubId())
                );
    }

    @Override
    public String getTypeOfPayload() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }
}
