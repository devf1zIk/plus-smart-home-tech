package ru.practicum.processor;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.entity.Action;
import ru.practicum.entity.Condition;
import ru.practicum.entity.ScenarioAction;
import ru.practicum.entity.ScenarioCondition;
import ru.practicum.entity.Scenario;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final ScenarioRepository scenarioRepository;

    @GrpcClient("hub-router")
    private HubRouterControllerBlockingStub hubRouterClient;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.snapshots}")
    private String snapshotsTopic;

    public void start() {
        log.info("Запуск SnapshotProcessor. Подписка на топик: {}", snapshotsTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshots-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.telemetry.serialization.SensorsSnapshotDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(snapshotsTopic));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(200));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    processSnapshot(snapshot);
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor: ", e);
        }
    }

    private void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.debug("Обработка снапшота хаба {}", hubId);

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            log.debug("Нет сценариев для хаба {}", hubId);
            return;
        }

        for (Scenario scenario : scenarios) {
            boolean conditionsMet = checkScenarioConditions(scenario, snapshot);

            if (conditionsMet) {
                log.info("Условия сценария '{}' выполнены. Отправляем действия...", scenario.getName());
                executeScenarioActions(hubId, scenario);
            } else {
                log.debug("Условия сценария '{}' не выполнены.", scenario.getName());
            }
        }
    }

    private boolean checkScenarioConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        if (scenario.getConditions().isEmpty()) {
            log.warn("Сценарий '{}' не содержит условий!", scenario.getName());
            return false;
        }

        for (ScenarioCondition sc : scenario.getConditions()) {
            String sensorId = sc.getSensor().getId();
            SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
            if (state == null) {
                log.debug("Сенсор {} отсутствует в снапшоте, пропускаем условие", sensorId);
                return false;
            }

            Condition condition = sc.getCondition();
            boolean result = evaluateCondition(condition, state.getData());
            if (!result) return false;
        }

        return true;
    }

    private boolean evaluateCondition(Condition condition, Object data) {
        String operation = condition.getOperation();
        Integer expected = condition.getValue();
        if (expected == null) return false;

        if (data instanceof TemperatureSensorAvro temp) {
            return compare(temp.getTemperatureC(), expected, operation);
        } else if (data instanceof ClimateSensorAvro climate) {
            return compare(climate.getTemperatureC(), expected, operation);
        } else if (data instanceof LightSensorAvro light) {
            return compare(light.getLuminosity(), expected, operation);
        } else if (data instanceof MotionSensorAvro motion) {
            return motion.getMotion() && expected == 1;
        } else if (data instanceof SwitchSensorAvro sw) {
            return sw.getState() == (expected == 1);
        }

        return false;
    }

    private boolean compare(int sensorValue, int expected, String operation) {
        return switch (operation) {
            case "GREATER_THAN" -> sensorValue > expected;
            case "LOWER_THAN" -> sensorValue < expected;
            case "EQUALS" -> sensorValue == expected;
            default -> false;
        };
    }

    private void executeScenarioActions(String hubId, Scenario scenario) {
        Instant now = Instant.now();

        for (ScenarioAction sa : scenario.getActions()) {
            Action action = sa.getAction();
            String sensorId = sa.getSensor().getId();

            Integer rawValue = action.getValue();
            int safeValue = (rawValue != null) ? rawValue : 0;

            if (rawValue == null) {
                log.debug("Действие {} для сенсора {} не содержит value, подставлено 0",
                        action.getType(), sensorId);
            }

            DeviceActionProto grpcAction = DeviceActionProto.newBuilder()
                    .setSensorId((sensorId))
                    .setType(ActionTypeProto.valueOf(action.getType()))
                    .setValue(safeValue)
                    .build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(grpcAction)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(now.getEpochSecond())
                            .setNanos(now.getNano())
                            .build())
                    .build();

            try {
                hubRouterClient.handleDeviceAction(request);
                log.info("Выполнено действие {} для сенсора {} (hubId={})",
                        action.getType(), safeValue, hubId);
            } catch (StatusRuntimeException e) {
                log.error("Ошибка при вызове gRPC HubRouter: {}", e.getStatus(), e);
            }
        }
    }
}