package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.entity.Scenario;
import ru.practicum.entity.ScenarioAction;
import ru.practicum.entity.ScenarioActionId;
import ru.practicum.entity.ScenarioCondition;
import ru.practicum.entity.ScenarioConditionId;
import ru.practicum.repository.ActionRepository;
import ru.practicum.repository.ConditionRepository;
import ru.practicum.repository.ScenarioRepository;
import ru.practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.practicum.entity.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.hub-events}")
    private String hubEventsTopic;

    @Override
    public void run() {
        log.info("Запуск HubEventProcessor. Подписка на топик: {}", hubEventsTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-hub-events-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.telemetry.deserialization.HubEventDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(hubEventsTopic));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processEvent(record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в HubEventProcessor: ", e);
        }
    }

    private void processEvent(HubEventAvro event) {
        log.debug("Получено событие HubEventAvro: {}", event);

        switch (event.getPayload().getClass().getSimpleName()) {
            case "DeviceAddedEventAvro" -> handleDeviceAdded(event);
            case "DeviceRemovedEventAvro" -> handleDeviceRemoved(event);
            case "ScenarioAddedEventAvro" -> handleScenarioAdded(event);
            case "ScenarioRemovedEventAvro" -> handleScenarioRemoved(event);
            default -> log.warn("Неизвестный тип события: {}", event.getPayload().getClass());
        }
    }

    private void handleDeviceAdded(HubEventAvro event) {
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

    private void handleDeviceRemoved(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();
        String sensorId = payload.getId();

        if (sensorRepository.existsById(sensorId)) {
            sensorRepository.deleteById(sensorId);
            log.info("Удалён сенсор {}", sensorId);
        } else {
            log.debug("Попытка удалить несуществующий сенсор {}", sensorId);
        }
    }

    private void handleScenarioAdded(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String name = payload.getName();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .build());

        scenarioRepository.save(scenario);

        scenario.getConditions().clear();
        scenario.getActions().clear();

        payload.getConditions().forEach(cond -> {
            Integer value = null;

            Object rawValue = cond.getValue();
            if (rawValue instanceof Integer i) {
                value = i;
            } else if (rawValue instanceof Boolean b) {
                value = b ? 1 : 0;
            }

            Condition condition = conditionRepository.save(Condition.builder()
                    .type(cond.getType().name())
                    .operation(cond.getOperation().name())
                    .value(value)
                    .build());

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
            Action action = actionRepository.save(Action.builder()
                    .type(act.getType().name())
                    .value(act.getValue() != null ? act.getValue() : null)
                    .build());

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

    private void handleScenarioRemoved(HubEventAvro event) {
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