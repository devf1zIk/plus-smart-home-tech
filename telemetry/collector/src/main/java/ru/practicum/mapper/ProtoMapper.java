package ru.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;

@Component
public class ProtoMapper {

    public HubEventAvro toAvro(HubEventProto proto) {
        var builder = HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(mapTimestamp(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case DEVICE_ADDED:
                var deviceAdded = proto.getDeviceAdded();
                var deviceAddedAvro = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAdded.getId())
                        .setType(mapDeviceType(deviceAdded.getType()))
                        .build();
                builder.setPayload(deviceAddedAvro);
                break;

            case DEVICE_REMOVED:
                var deviceRemoved = proto.getDeviceRemoved();
                var deviceRemovedAvro = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemoved.getId())
                        .build();
                builder.setPayload(deviceRemovedAvro);
                break;

            case SCENARIO_ADDED:
                var scenarioAdded = proto.getScenarioAdded();
                var scenarioAddedAvro = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAdded.getName())
                        .setConditions(mapConditions(scenarioAdded.getConditionList()))
                        .setActions(mapActions(scenarioAdded.getActionList()))
                        .build();
                builder.setPayload(scenarioAddedAvro);
                break;

            case SCENARIO_REMOVED:
                var scenarioRemoved = proto.getScenarioRemoved();
                var scenarioRemovedAvro = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemoved.getName())
                        .build();
                builder.setPayload(scenarioRemovedAvro);
                break;

            case PAYLOAD_NOT_SET:
            default:
                var defaultPayload = DeviceAddedEventAvro.newBuilder()
                        .setId("default")
                        .setType(DeviceTypeAvro.MOTION_SENSOR)
                        .build();
                builder.setPayload(defaultPayload);
                break;
        }

        return builder.build();
    }

    public SensorEventAvro toAvro(SensorEventProto proto) {
        var builder = SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setSensorId(proto.getSensorId())
                .setTimestamp(mapTimestamp(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT:
                var motion = proto.getMotionSensorEvent();
                var motionAvro = MotionSensorAvro.newBuilder()
                        .setLinkQuality(motion.getLinkQuality())
                        .setMotion(motion.getMotion())
                        .setVoltage(motion.getVoltage())
                        .build();
                builder.setPayload(motionAvro);
                break;

            case TEMPERATURE_SENSOR_EVENT:
                var temp = proto.getTemperatureSensorEvent();
                var tempAvro = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temp.getTemperatureC())
                        .setTemperatureF(temp.getTemperatureF())
                        .build();
                builder.setPayload(tempAvro);
                break;

            case LIGHT_SENSOR_EVENT:
                var light = proto.getLightSensorEvent();
                var lightAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(light.getLinkQuality())
                        .setLuminosity(light.getLuminosity())
                        .build();
                builder.setPayload(lightAvro);
                break;

            case CLIMATE_SENSOR_EVENT:
                var climate = proto.getClimateSensorEvent();
                var climateAvro = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(climate.getTemperatureC())
                        .setHumidity(climate.getHumidity())
                        .setCo2Level(climate.getCo2Level())
                        .build();
                builder.setPayload(climateAvro);
                break;

            case SWITCH_SENSOR_EVENT:
                var switchEvent = proto.getSwitchSensorEvent();
                var switchAvro = SwitchSensorAvro.newBuilder()
                        .setState(switchEvent.getState())
                        .build();
                builder.setPayload(switchAvro);
                break;

            case PAYLOAD_NOT_SET:
            default:
                var defaultPayload = MotionSensorAvro.newBuilder()
                        .setLinkQuality(0)
                        .setMotion(false)
                        .setVoltage(0)
                        .build();
                builder.setPayload(defaultPayload);
                break;
        }

        return builder.build();
    }

    private ScenarioConditionAvro mapCondition(ScenarioConditionProto condition) {
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

        return builder.build();
    }

    private DeviceActionAvro mapAction(DeviceActionProto action) {
        var builder = DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapActionType(action.getType()));

        if (action.hasValue()) {
            builder.setValue(action.getValue());
        } else {
            builder.setValue(null);
        }

        return builder.build();
    }

    private long mapTimestamp(Timestamp timestamp) {
        if (timestamp == null || timestamp.getSeconds() == 0) {
            return Instant.now().toEpochMilli();
        }
        long seconds = timestamp.getSeconds();
        int nanos = timestamp.getNanos();
        return seconds * 1000 + nanos / 1_000_000;
    }

    private DeviceTypeAvro mapDeviceType(DeviceTypeProto type) {
        if (type == null) return DeviceTypeAvro.MOTION_SENSOR;
        return DeviceTypeAvro.valueOf(type.name());
    }

    private java.util.List<ScenarioConditionAvro> mapConditions(
            java.util.List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(this::mapCondition)
                .collect(java.util.stream.Collectors.toList());
    }

    private java.util.List<DeviceActionAvro> mapActions(
            java.util.List<DeviceActionProto> actions) {
        return actions.stream()
                .map(this::mapAction)
                .collect(java.util.stream.Collectors.toList());
    }

    private ConditionTypeAvro mapConditionType(ConditionTypeProto type) {
        if (type == null) return ConditionTypeAvro.MOTION;
        return ConditionTypeAvro.valueOf(type.name());
    }

    private ConditionOperationAvro mapConditionOperation(ConditionOperationProto operation) {
        if (operation == null) return ConditionOperationAvro.EQUALS;
        return ConditionOperationAvro.valueOf(operation.name());
    }

    private ActionTypeAvro mapActionType(ActionTypeProto type) {
        if (type == null) return ActionTypeAvro.ACTIVATE;
        return ActionTypeAvro.valueOf(type.name());
    }
}