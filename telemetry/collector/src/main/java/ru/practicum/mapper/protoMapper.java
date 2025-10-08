package ru.practicum.mapper;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class protoMapper {

    public HubEventAvro toAvro(HubEventProto proto) {
        log.info("Mapping HubEventProto to Avro: hubId={}, payloadCase={}",
                proto.getHubId(), proto.getPayloadCase());

        var builder = HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(map(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case DEVICE_ADDED:
                log.debug("Mapping DEVICE_ADDED payload");
                var deviceAdded = proto.getDeviceAdded();
                var deviceAddedAvro = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAdded.getId())
                        .setType(mapDeviceType(deviceAdded.getType()))
                        .build();
                builder.setPayload(deviceAddedAvro);
                break;

            case DEVICE_REMOVED:
                log.debug("Mapping DEVICE_REMOVED payload");
                var deviceRemoved = proto.getDeviceRemoved();
                var deviceRemovedAvro = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemoved.getId())
                        .build();
                builder.setPayload(deviceRemovedAvro);
                break;

            case SCENARIO_ADDED:
                log.debug("Mapping SCENARIO_ADDED payload");
                var scenarioAdded = proto.getScenarioAdded();
                var scenarioAddedAvro = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAdded.getName())
                        .setConditions(mapConditions(scenarioAdded.getConditionList()))
                        .setActions(mapActions(scenarioAdded.getActionList()))
                        .build();
                builder.setPayload(scenarioAddedAvro);
                break;

            case SCENARIO_REMOVED:
                log.debug("Mapping SCENARIO_REMOVED payload");
                var scenarioRemoved = proto.getScenarioRemoved();
                var scenarioRemovedAvro = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemoved.getName())
                        .build();
                builder.setPayload(scenarioRemovedAvro);
                break;

            case PAYLOAD_NOT_SET:
            default:
                log.warn("HubEvent payload not set, using default payload");
                var defaultPayload = DeviceAddedEventAvro.newBuilder()
                        .setId("default")
                        .setType(DeviceTypeAvro.MOTION_SENSOR)
                        .build();
                builder.setPayload(defaultPayload);
                break;
        }

        var result = builder.build();
        log.info("HubEventAvro successfully mapped: hubId={}, payloadType={}",
                result.getHubId(), result.getPayload().getClass().getSimpleName());
        return result;
    }

    public SensorEventAvro toAvro(SensorEventProto proto) {
        log.info("Mapping SensorEventProto to Avro: id={}, hubId={}, payloadCase={}",
                proto.getId(), proto.getHubId(), proto.getPayloadCase());

        var builder = SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(map(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT:
                log.debug("Mapping MOTION_SENSOR_EVENT payload");
                var motion = proto.getMotionSensorEvent();
                var motionAvro = MotionSensorAvro.newBuilder()
                        .setLinkQuality(motion.getLinkQuality())
                        .setMotion(motion.getMotion())
                        .setVoltage(motion.getVoltage())
                        .build();
                builder.setPayload(motionAvro);
                break;

            case TEMPERATURE_SENSOR_EVENT:
                log.debug("Mapping TEMPERATURE_SENSOR_EVENT payload");
                var temp = proto.getTemperatureSensorEvent();
                var tempAvro = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temp.getTemperatureC())
                        .setTemperatureF(temp.getTemperatureF())
                        .build();
                builder.setPayload(tempAvro);
                break;

            case LIGHT_SENSOR_EVENT:
                log.debug("Mapping LIGHT_SENSOR_EVENT payload");
                var light = proto.getLightSensorEvent();
                var lightAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(light.getLinkQuality())
                        .setLuminosity(light.getLuminosity())
                        .build();
                builder.setPayload(lightAvro);
                break;

            case CLIMATE_SENSOR_EVENT:
                log.debug("Mapping CLIMATE_SENSOR_EVENT payload");
                var climate = proto.getClimateSensorEvent();
                var climateAvro = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(climate.getTemperatureC())
                        .setHumidity(climate.getHumidity())
                        .setCo2Level(climate.getCo2Level())
                        .build();
                builder.setPayload(climateAvro);
                break;

            case SWITCH_SENSOR_EVENT:
                log.debug("Mapping SWITCH_SENSOR_EVENT payload");
                var switchEvent = proto.getSwitchSensorEvent();
                var switchAvro = SwitchSensorAvro.newBuilder()
                        .setState(switchEvent.getState())
                        .build();
                builder.setPayload(switchAvro);
                break;

            case PAYLOAD_NOT_SET:
            default:
                log.warn("SensorEvent payload not set, using default payload");
                var defaultPayload = MotionSensorAvro.newBuilder()
                        .setLinkQuality(0)
                        .setMotion(false)
                        .setVoltage(0)
                        .build();
                builder.setPayload(defaultPayload);
                break;
        }

        var result = builder.build();
        log.info("SensorEventAvro mapped: id={}, hubId={}, payloadType={}",
                result.getId(), result.getHubId(), result.getPayload().getClass().getSimpleName());
        return result;
    }

    private ScenarioConditionAvro mapCondition(ScenarioConditionProto condition) {
        log.debug("Mapping ScenarioConditionProto: sensorId={}, type={}, operation={}",
                condition.getSensorId(), condition.getType(), condition.getOperation());

        var builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapConditionType(condition.getType()))
                .setOperation(mapConditionOperation(condition.getOperation()));

        switch (condition.getValueCase()) {
            case INT_VALUE:
                builder.setValue(condition.getIntValue());
                break;
            case BOOL_VALUE:
                builder.setValue(condition.getBoolValue());
                break;
            case VALUE_NOT_SET:
            default:
                builder.setValue(null);
                break;
        }

        var result = builder.build();
        log.debug("ScenarioConditionAvro mapped: {}", result);
        return result;
    }

    private Instant map(Timestamp ts) {
        if (ts == null) return null;
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }

    private DeviceActionAvro mapAction(DeviceActionProto action) {
        log.debug("Mapping DeviceActionProto: sensorId={}, type={}",
                action.getSensorId(), action.getType());
        var builder = DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapActionType(action.getType()));
        if (action.hasValue()) {
            builder.setValue(action.getValue());
        } else {
            builder.setValue(null);
        }
        var result = builder.build();
        log.debug("DeviceActionAvro mapped: {}", result);
        return result;
    }

    private DeviceTypeAvro mapDeviceType(DeviceTypeProto type) {
        var result = (type == null) ? DeviceTypeAvro.MOTION_SENSOR : DeviceTypeAvro.valueOf(type.name());
        log.trace("Mapped DeviceType: {} -> {}", type, result);
        return result;
    }

    private List<ScenarioConditionAvro> mapConditions(List<ScenarioConditionProto> conditions) {
        log.debug("Mapping list of ScenarioConditionProto, size={}", conditions.size());
        return conditions.stream().map(this::mapCondition).collect(Collectors.toList());
    }

    private List<DeviceActionAvro> mapActions(List<DeviceActionProto> actions) {
        log.debug("Mapping list of DeviceActionProto, size={}", actions.size());
        return actions.stream().map(this::mapAction).collect(Collectors.toList());
    }

    private ConditionTypeAvro mapConditionType(ConditionTypeProto type) {
        var result = (type == null) ? ConditionTypeAvro.MOTION : ConditionTypeAvro.valueOf(type.name());
        log.trace("Mapped ConditionType: {} -> {}", type, result);
        return result;
    }

    private ConditionOperationAvro mapConditionOperation(ConditionOperationProto operation) {
        var result = (operation == null) ? ConditionOperationAvro.EQUALS : ConditionOperationAvro.valueOf(operation.name());
        log.trace("Mapped ConditionOperation: {} -> {}", operation, result);
        return result;
    }

    private ActionTypeAvro mapActionType(ActionTypeProto type) {
        var result = (type == null) ? ActionTypeAvro.ACTIVATE : ActionTypeAvro.valueOf(type.name());
        log.trace("Mapped ActionType: {} -> {}", type, result);
        return result;
    }
}