package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.*;
import ru.practicum.enums.ActionType;
import ru.practicum.enums.ConditionOperation;
import ru.practicum.enums.ConditionType;
import ru.practicum.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import java.util.HashSet;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;


    @Transactional
    public void upsertScenarioFromEvent(ScenarioAddedEventAvro payload,
                                        String hubId) {
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(hubId, "hubId must not be null");

        String name = payload.getName();
        log.debug("Upsert scenario for hubId='{}', name='{}'", hubId, name);

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> {
                    Scenario newScenario = Scenario.builder()
                            .hubId(hubId)
                            .name(name)
                            .build();
                    Scenario saved = scenarioRepository.save(newScenario);
                    log.info("Создан новый сценарий id={} hubId={} name={}", saved.getId(), hubId, name);
                    return saved;
                });

        scenario = scenarioRepository.findById(scenario.getId()).orElse(scenario);

        if (scenario.getConditions() == null) {
            log.debug("Инициализируем пустой набор conditions для scenario id={}", scenario.getId());
            scenario.setConditions(new HashSet<>());
        }
        if (scenario.getActions() == null) {
            log.debug("Инициализируем пустой набор actions для scenario id={}", scenario.getId());
            scenario.setActions(new HashSet<>());
        }

        if (payload.getConditions() != null) {
            for (var cond : payload.getConditions()) {
                if (cond == null) {
                    log.warn("Пропускаю null условие в payload для сценария '{}'", name);
                    continue;
                }

                Integer value = null;
                Object rawValue = cond.getValue();
                if (rawValue instanceof Integer i) value = i;
                else if (rawValue instanceof Boolean b) value = b ? 1 : 0;

                ConditionType type;
                ConditionOperation operation;
                try {
                    type = ConditionType.valueOf(cond.getType().name());
                    operation = ConditionOperation.valueOf(cond.getOperation().name());
                } catch (Exception e) {
                    log.error("Не удалось распарсить тип/операцию для условия: {}, пропускаю. error={}",
                            cond, e, e);
                    continue;
                }

                Condition condition = conditionRepository.save(
                        Condition.builder()
                                .type(type)
                                .operation(operation)
                                .value(value)
                                .build()
                );

                Scenario finalScenario = scenario;
                sensorRepository.findByIdAndHubId(cond.getSensorId(), hubId).ifPresentOrElse(sensor -> {
                    if (finalScenario.getConditions() == null) {
                        finalScenario.setConditions(new HashSet<>());
                    }

                    ScenarioCondition sc = ScenarioCondition.builder()
                            .id(new ScenarioConditionId(finalScenario.getId(), sensor.getId(), condition.getId()))
                            .scenario(finalScenario)
                            .sensor(sensor)
                            .condition(condition)
                            .build();
                    finalScenario.getConditions().add(sc);
                    log.debug("Добавлено условие (conditionId={}) к сценарию id={} для сенсора {}",
                            condition.getId(), finalScenario.getId(), sensor.getId());
                }, () -> log.warn("Сенсор с id={} не найден в хабе {} — условие не прикреплено к сценарию '{}'",
                        cond.getSensorId(), hubId, name));
            }
        } else {
            log.debug("payload.getConditions() == null для сценария '{}'", name);
        }

        if (payload.getActions() != null) {
            for (var act : payload.getActions()) {
                if (act == null) {
                    log.warn("Пропускаю null действие в payload для сценария '{}'", name);
                    continue;
                }

                ActionType type;
                try {
                    type = ActionType.valueOf(act.getType().name());
                } catch (Exception e) {
                    log.error("Не удалось распарсить тип действия: {}, пропускаю. error={}", act, e.toString(), e);
                    continue;
                }

                Action action = actionRepository.save(
                        Action.builder()
                                .type(type)
                                .value(act.getValue() != null ? act.getValue() : null)
                                .build()
                );

                Scenario finalScenario = scenario;
                sensorRepository.findByIdAndHubId(act.getSensorId(), hubId).ifPresentOrElse(sensor -> {
                    if (finalScenario.getActions() == null) {
                        finalScenario.setActions(new HashSet<>());
                    }

                    ScenarioAction sa = ScenarioAction.builder()
                            .id(new ScenarioActionId(finalScenario.getId(), sensor.getId(), action.getId()))
                            .scenario(finalScenario)
                            .sensor(sensor)
                            .action(action)
                            .build();
                    finalScenario.getActions().add(sa);
                    log.debug("Добавлено действие (actionId={}) к сценарию id={} для сенсора {}",
                            action.getId(), finalScenario.getId(), sensor.getId());
                }, () -> log.warn("Сенсор с id={} не найден в хабе {} — действие не прикреплено к сценарию '{}'",
                        act.getSensorId(), hubId, name));
            }
        } else {
            log.debug("payload.getActions() == null для сценария '{}'", name);
        }

        scenarioRepository.save(scenario);
        log.info("Сценарий '{}' (id={}) обновлён/сохранён для hubId={}", name, scenario.getId(), hubId);
    }
}
