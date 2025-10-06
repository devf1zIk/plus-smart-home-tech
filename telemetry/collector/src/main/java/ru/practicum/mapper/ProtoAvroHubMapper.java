package ru.practicum.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProtoAvroHubMapper {

    public HubEventAvro toAvro(HubEventProto proto) {
        if (proto == null) {
            throw new IllegalArgumentException("HubEventProto cannot be null");
        }

        HubEventAvro.Builder builder = HubEventAvro.newBuilder();

        builder.setHubId(proto.getHubId());

        Instant timestamp;
        if (proto.hasTimestamp()) {
            long seconds = proto.getTimestamp().getSeconds();
            int nanos = proto.getTimestamp().getNanos();
            timestamp = Instant.ofEpochSecond(seconds, nanos);
        } else {
            timestamp = Instant.now();
        }
        builder.setTimestamp(timestamp);

        SpecificRecordBase payload = createPayload(proto);
        builder.setPayload(payload);

        return builder.build();
    }

    private SpecificRecordBase createPayload(HubEventProto proto) {
        HubEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        if (payloadCase == HubEventProto.PayloadCase.DEVICE_ADDED) {
            return createDeviceAddedPayload(proto.getDeviceAdded());
        }

        if (payloadCase == HubEventProto.PayloadCase.DEVICE_REMOVED) {
            return createDeviceRemovedPayload(proto.getDeviceRemoved());
        }

        if (payloadCase == HubEventProto.PayloadCase.SCENARIO_ADDED) {
            return createScenarioAddedPayload(proto.getScenarioAdded());
        }

        if (payloadCase == HubEventProto.PayloadCase.SCENARIO_REMOVED) {
            return createScenarioRemovedPayload(proto.getScenarioRemoved());
        }

        if (payloadCase == HubEventProto.PayloadCase.PAYLOAD_NOT_SET) {
            throw new IllegalArgumentException("Hub payload is empty for hubId: " + proto.getHubId());
        }

        throw new IllegalStateException("Unknown hub payload case: " + payloadCase);
    }

    private DeviceAddedEventAvro createDeviceAddedPayload(DeviceAddedEventProto proto) {
        DeviceAddedEventAvro.Builder builder = DeviceAddedEventAvro.newBuilder();
        builder.setId(proto.getId());
        builder.setType(DeviceTypeAvro.valueOf(proto.getType().name()));
        return builder.build();
    }

    private DeviceRemovedEventAvro createDeviceRemovedPayload(DeviceRemovedEventProto proto) {
        DeviceRemovedEventAvro.Builder builder = DeviceRemovedEventAvro.newBuilder();
        builder.setId(proto.getId());
        return builder.build();
    }

    private ScenarioAddedEventAvro createScenarioAddedPayload(ScenarioAddedEventProto proto) {
        ScenarioAddedEventAvro.Builder builder = ScenarioAddedEventAvro.newBuilder();
        builder.setName(proto.getName());

        // Маппим условия
        List<ScenarioConditionAvro> conditions = new ArrayList<>();
        for (ScenarioConditionProto conditionProto : proto.getConditionList()) {
            ScenarioConditionAvro condition = createScenarioCondition(conditionProto);
            conditions.add(condition);
        }
        builder.setConditions(conditions);

        // Маппим действия
        List<DeviceActionAvro> actions = new ArrayList<>();
        for (DeviceActionProto actionProto : proto.getActionList()) {
            DeviceActionAvro action = createDeviceAction(actionProto);
            actions.add(action);
        }
        builder.setActions(actions);

        return builder.build();
    }

    private ScenarioRemovedEventAvro createScenarioRemovedPayload(ScenarioRemovedEventProto proto) {
        ScenarioRemovedEventAvro.Builder builder = ScenarioRemovedEventAvro.newBuilder();
        builder.setName(proto.getName());
        return builder.build();
    }

    private ScenarioConditionAvro createScenarioCondition(ScenarioConditionProto proto) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder();

        builder.setSensorId(proto.getSensorId());
        builder.setType(ConditionTypeAvro.valueOf(proto.getType().name()));
        builder.setOperation(ConditionOperationAvro.valueOf(proto.getOperation().name()));

        if (proto.getValueCase() == ScenarioConditionProto.ValueCase.BOOL_VALUE) {
            builder.setValue(proto.getBoolValue());
        }
        else if (proto.getValueCase() == ScenarioConditionProto.ValueCase.INT_VALUE) {
            builder.setValue(proto.getIntValue());
        }
        else if (proto.getValueCase() == ScenarioConditionProto.ValueCase.VALUE_NOT_SET) {
            builder.setValue(null);
        }
        else {
            throw new IllegalStateException("Unknown condition value case: " + proto.getValueCase());
        }

        return builder.build();
    }

    private DeviceActionAvro createDeviceAction(DeviceActionProto proto) {
        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder();

        builder.setSensorId(proto.getSensorId());
        builder.setType(ActionTypeAvro.valueOf(proto.getType().name()));

        if (proto.hasValue()) {
            builder.setValue(proto.getValue());
        } else {
            builder.setValue(null);
        }

        return builder.build();
    }
}