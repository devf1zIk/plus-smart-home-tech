package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.entity.*;
import ru.practicum.enums.ActionType;
import ru.practicum.enums.ConditionOperation;
import ru.practicum.enums.ConditionType;
import ru.practicum.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    public void handleDeviceAdded(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();
        String sensorId = payload.getId();
        String hubId = event.getHubId();

        sensorRepository.findById(sensorId).ifPresentOrElse(
                s -> log.debug("Сенсор {} уже существует в хабе {}", sensorId, hubId),
                () -> {
                    sensorRepository.save(Sensor.builder()
                            .id(sensorId)
                            .hubId(hubId)
                            .build());
                    log.info("Добавлен новый сенсор {} в хаб {}", sensorId, hubId);
                });
    }

    @Override
    public void handleDeviceRemoved(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();
        String sensorId = payload.getId();

        if (sensorRepository.existsById(sensorId)) {
            sensorRepository.deleteById(sensorId);
            log.info("Удалён сенсор {}", sensorId);
        } else {
            log.debug("Попытка удалить несуществующий сенсор {}", sensorId);
        }
    }

    @Override
    public void handleScenarioAdded(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String name = payload.getName();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> scenarioRepository.save(
                        Scenario.builder().hubId(hubId).name(name).build()
                ));

        scenario.getConditions().clear();
        scenario.getActions().clear();

        payload.getConditions().forEach(cond -> {
            Integer value = null;

            Condition condition = conditionRepository.save(
                    Condition.builder()
                            .type(ConditionType.valueOf(cond.getType().name()))
                            .operation(ConditionOperation.valueOf(cond.getOperation().name()))
                            .value(value)
                            .build()
            );

            sensorRepository.findByIdAndHubId(cond.getSensorId(), hubId).ifPresent(sensor -> {
                ScenarioCondition sc = ScenarioCondition.builder()
                        .id(new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId()))
                        .scenario(scenario)
                        .sensor(sensor)
                        .condition(condition)
                        .build();
                scenario.getConditions().add(sc);
            });
        });

        payload.getActions().forEach(act -> {
            Action action = actionRepository.save(
                    Action.builder()
                            .type(ActionType.valueOf(act.getType().name()))
                            .value(act.getValue())
                            .build()
            );

            sensorRepository.findByIdAndHubId(act.getSensorId(), hubId).ifPresent(sensor -> {
                ScenarioAction sa = ScenarioAction.builder()
                        .id(new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId()))
                        .scenario(scenario)
                        .sensor(sensor)
                        .action(action)
                        .build();
                scenario.getActions().add(sa);
            });
        });

        scenarioRepository.save(scenario);

        log.info("Добавлен сценарий {} для хаба {} ({} условий, {} действий)",
                name, hubId, payload.getConditions().size(), payload.getActions().size());
    }

    @Override
    public void handleScenarioRemoved(HubEventAvro event) {
        ScenarioRemovedEventAvro payload = (ScenarioRemovedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String name = payload.getName();

        scenarioRepository.findByHubIdAndName(hubId, name).ifPresentOrElse(
                s -> {
                    scenarioRepository.delete(s);
                    log.info("Удалён сценарий {} из хаба {}", name, hubId);
                },
                () -> log.debug("Сценарий {} для хаба {} не найден", name, hubId)
        );
    }
}
