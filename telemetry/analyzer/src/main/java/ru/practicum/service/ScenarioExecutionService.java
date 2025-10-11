package ru.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.*;
import ru.practicum.enums.ConditionOperation;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.grpc.telemetry.hubrouter.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioExecutionService {

    private final ScenarioRepository scenarioRepository;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Transactional
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Сценариев для хаба {} нет", hubId);
            return;
        }
        Instant timestamp = Instant.ofEpochSecond(snapshot.getTimestamp().getEpochSecond(), snapshot.getTimestamp().getNano());

        for (Scenario scenario : scenarios) {
            boolean conditionsMet = scenario.getConditions().stream().allMatch(sc -> {
                SensorStateAvro state = snapshot.getSensorsState().get(sc.getSensor().getId());
                if (state == null) return false;
                return evaluateCondition(sc.getCondition(), state.getData());
            });

            if (conditionsMet) executeScenarioActions(hubId, timestamp,scenario);
        }
    }

    private boolean evaluateCondition(Condition condition, Object data) {
        if (condition == null || condition.getValue() == null) return false;
        int expected = condition.getValue();

        ConditionOperation op = ConditionOperation.valueOf(String.valueOf(condition.getOperation()));
        switch (op) {
            case EQUALS -> {
                if (data instanceof Boolean b) return (b ? 1 : 0) == expected;
                if (data instanceof Integer i) return i == expected;
            }
            case GREATER_THAN -> {
                if (data instanceof Integer i) return i > expected;
            }
            case LOWER_THAN -> {
                if (data instanceof Integer i) return i < expected;
            }
        }
        return false;
    }

    private void executeScenarioActions(String hubId, Instant timestamp, Scenario scenario) {
        log.debug("Начинаем выполнение действий сценария '{}' для хаба {}", scenario.getName(), hubId);

        for (ScenarioAction sa : scenario.getActions()) {
            Action action = sa.getAction();
            if (action == null || sa.getSensor() == null) {
                log.warn("Пропускаем действие: action или sensor равны null (ScenarioAction id={})", sa.getId());
                continue;
            }

            String sensorId = sa.getSensor().getId();
            int value = action.getValue() != null ? action.getValue() : 0;

            DeviceActionProto grpcAction = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(ActionTypeProto.valueOf(action.getType().name()))
                    .setValue(value)
                    .build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(grpcAction)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(timestamp.getEpochSecond())
                            .setNanos(timestamp.getNano())
                            .build())
                    .build();

            try {
                hubRouterClient.handleDeviceAction(request);
                log.info("Выполнено действие '{}' для сенсора '{}' (hubId={})",
                        action.getType(), sensorId, hubId);
            } catch (Exception e) {
                log.error("Ошибка при отправке gRPC-команды для сенсора {}: {}", sensorId, e.getMessage(), e);
            }
        }
        log.debug("Завершено выполнение действий сценария '{}' для хаба {}", scenario.getName(), hubId);
    }
}
