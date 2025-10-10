package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.*;
import ru.practicum.repository.*;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Transactional
    public void upsertScenarioFromEvent(ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro payload,
                                        String hubId) {
        String name = payload.getName();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> scenarioRepository.save(Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .build()));

        scenario = scenarioRepository.findById(scenario.getId()).orElse(scenario);

        for (var cond : payload.getConditions()) {
            Integer value = null;
            Object rawValue = cond.getValue();
            if (rawValue instanceof Integer i) value = i;
            else if (rawValue instanceof Boolean b) value = b ? 1 : 0;

            Condition condition = conditionRepository.save(Condition.builder()
                    .type(cond.getType().name())
                    .operation(cond.getOperation().name())
                    .value(value)
                    .build());

            Scenario finalScenario1 = scenario;
            sensorRepository.findByIdAndHubId(cond.getSensorId(), hubId).ifPresent(sensor -> {
                ScenarioCondition sc = ScenarioCondition.builder()
                        .id(new ScenarioConditionId(finalScenario1.getId(), sensor.getId(), condition.getId()))
                        .scenario(finalScenario1)
                        .sensor(sensor)
                        .condition(condition)
                        .build();
                finalScenario1.getConditions().add(sc);
            });
        }

        for (var act : payload.getActions()) {
            Action action = actionRepository.save(Action.builder()
                    .type(act.getType().name())
                    .value(act.getValue() != null ? act.getValue() : null)
                    .build());

            Scenario finalScenario = scenario;
            sensorRepository.findByIdAndHubId(act.getSensorId(), hubId).ifPresent(sensor -> {
                ScenarioAction sa = ScenarioAction.builder()
                        .id(new ScenarioActionId(finalScenario.getId(), sensor.getId(), action.getId()))
                        .scenario(finalScenario)
                        .sensor(sensor)
                        .action(action)
                        .build();
                finalScenario.getActions().add(sa);
            });
        }

        scenarioRepository.save(scenario);
    }
}
