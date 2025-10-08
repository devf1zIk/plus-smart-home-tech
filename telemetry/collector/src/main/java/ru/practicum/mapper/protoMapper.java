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
        var builder = HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(map(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case DEVICE_ADDED:
                var deviceAdded = proto.getDeviceAdded();
                var deviceAddedAvro = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAdded.getId())
                        .setType(mapDeviceType(deviceAdded.getType()))
                        .build();
                builder.setPayload(deviceAddedAvro);
                log.debug("Mapped DEVICE_ADDED event: id={}, type={}", deviceAdded.getId(), deviceAdded.getType());
                break;


            case DEVICE_REMOVED:
                var deviceRemoved = proto.getDeviceRemoved();
                var deviceRemovedAvro = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemoved.getId())
                        .build();
                builder.setPayload(deviceRemovedAvro);
                log.debug("Mapped DEVICE_REMOVED event: id={}", deviceRemoved.getId());
                break;


            case SCENARIO_ADDED:
                var scenarioAdded = proto.getScenarioAdded();
                var scenarioAddedAvro = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAdded.getName())
                        .setConditions(mapConditions(scenarioAdded.getConditionList()))
                        .setActions(mapActions(scenarioAdded.getActionList()))
                        .build();
                builder.setPayload(scenarioAddedAvro);
                log.debug("Mapped SCENARIO_ADDED event: name={}", scenarioAdded.getName());
                break;


            case SCENARIO_REMOVED:
                var scenarioRemoved = proto.getScenarioRemoved();
                var scenarioRemovedAvro = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemoved.getName())
                        .build();
                builder.setPayload(scenarioRemovedAvro);
                log.debug("Mapped SCENARIO_REMOVED event: name={}", scenarioRemoved.getName());
                break;


            case PAYLOAD_NOT_SET:
            default:
                log.warn("Unknown or unset payload case for HubEvent: {}", proto.getPayloadCase());
                break;
        }

        return builder.build();
    }

    public SensorEventAvro toAvro(SensorEventProto proto) {
        var builder = SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(map(proto.getTimestamp()));


        switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT:
                var motion = proto.getMotionSensorEvent();
                var motionAvro = MotionSensorAvro.newBuilder()
                        .setLinkQuality(motion.getLinkQuality())
                        .setMotion(motion.getMotion())
                        .setVoltage(motion.getVoltage())
                        .build();
                builder.setPayload(motionAvro);
                log.debug("Mapped MOTION_SENSOR_EVENT: id={}, motion={}", proto.getId(), motion.getMotion());
                break;


            case TEMPERATURE_SENSOR_EVENT:
                var temp = proto.getTemperatureSensorEvent();
                var tempAvro = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temp.getTemperatureC())
                        .setTemperatureF(temp.getTemperatureF())
                        .build();
                builder.setPayload(tempAvro);
                log.debug("Mapped TEMPERATURE_SENSOR_EVENT: id={}, temp={}°C", proto.getId(), temp.getTemperatureC());
                break;


            case LIGHT_SENSOR_EVENT:
                var light = proto.getLightSensorEvent();
                var lightAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(light.getLinkQuality())
                        .setLuminosity(light.getLuminosity())
                        .build();
                builder.setPayload(lightAvro);
                log.debug("Mapped LIGHT_SENSOR_EVENT: id={}, luminosity={}", proto.getId(), light.getLuminosity());
                break;


            case CLIMATE_SENSOR_EVENT:
                var climate = proto.getClimateSensorEvent();
                var climateAvro = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(climate.getTemperatureC())
                        .setHumidity(climate.getHumidity())
                        .setCo2Level(climate.getCo2Level())
                        .build();
                builder.setPayload(climateAvro);
                log.debug("Mapped CLIMATE_SENSOR_EVENT: id={}, temp={}°C, humidity={}%, co2={}",
                        proto.getId(), climate.getTemperatureC(), climate.getHumidity(), climate.getCo2Level());
                break;


            case SWITCH_SENSOR_EVENT:
                var switchEvent = proto.getSwitchSensorEvent();
                var switchAvro = SwitchSensorAvro.newBuilder()
                        .setState(switchEvent.getState())
                        .build();
                builder.setPayload(switchAvro);
                log.debug("Mapped SWITCH_SENSOR_EVENT: id={}, state={}", proto.getId(), switchEvent.getState());
                break;


            case PAYLOAD_NOT_SET:
            default:
                log.warn("Unknown or unset payload case for SensorEvent: {}", proto.getPayloadCase());
                break;
        }


        SensorEventAvro result = builder.build();


        if (result.getPayload() != null) {
            log.debug("Final SensorEventAvro payload type: {}", result.getPayload().getClass().getSimpleName());
        } else {
            log.warn("SensorEventAvro payload is null for event id: {}", proto.getId());
        }


        return result;
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

    private Instant map(Timestamp ts) {
        if (ts == null) return null;
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
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

    private DeviceTypeAvro mapDeviceType(DeviceTypeProto type) {
        if (type == null) return DeviceTypeAvro.MOTION_SENSOR;
        try {
            return DeviceTypeAvro.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown device type: {}, defaulting to MOTION_SENSOR", type);
            return DeviceTypeAvro.MOTION_SENSOR;
        }
    }

    private List<ScenarioConditionAvro> mapConditions(List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(this::mapCondition)
                .collect(Collectors.toList());
    }

    private List<DeviceActionAvro> mapActions(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(this::mapAction)
                .collect(Collectors.toList());
    }

    private ConditionTypeAvro mapConditionType(ConditionTypeProto type) {
        if (type == null) return ConditionTypeAvro.MOTION;
        try {
            return ConditionTypeAvro.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown condition type: {}, defaulting to MOTION", type);
            return ConditionTypeAvro.MOTION;
        }
    }

    private ConditionOperationAvro mapConditionOperation(ConditionOperationProto operation) {
        if (operation == null) return ConditionOperationAvro.EQUALS;
        try {
            return ConditionOperationAvro.valueOf(operation.name());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown condition operation: {}, defaulting to EQUALS", operation);
            return ConditionOperationAvro.EQUALS;
        }
    }

    private ActionTypeAvro mapActionType(ActionTypeProto type) {
        if (type == null) return ActionTypeAvro.ACTIVATE;
        try {
            return ActionTypeAvro.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown action type: {}, defaulting to ACTIVATE", type);
            return ActionTypeAvro.ACTIVATE;
        }
    }
}
