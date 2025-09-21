package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.event.hub.base.HubEvent;
import ru.practicum.event.hub.enums.*;
import ru.practicum.event.hub.device.DeviceAction;
import ru.practicum.event.hub.device.DeviceAddedEvent;
import ru.practicum.event.hub.device.DeviceRemovedEvent;
import ru.practicum.event.hub.scenario.ScenarioAddedEvent;
import ru.practicum.event.hub.scenario.ScenarioCondition;
import ru.practicum.event.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class HubEventMapper {

    public static HubEventAvro toAvro(HubEvent e) {
        if (e == null) throw new IllegalArgumentException("hub event is null");

        Instant ts = (e.getTimestamp() != null) ? e.getTimestamp() : Instant.now();

        return HubEventAvro.newBuilder()
                .setHubId(e.getHubId())
                .setTimestamp(ts)
                .setPayload(buildPayload(e))
                .build();
    }

    public static SpecificRecordBase buildPayload(HubEvent e) {
        HubEventType t = e.getType();

        if (t == HubEventType.DEVICE_ADDED) {
            DeviceAddedEvent ev = (DeviceAddedEvent) e;
            return DeviceAddedEventAvro.newBuilder()
                    .setId(ev.getId())
                    .setType(map(ev.getDeviceType()))
                    .build();
        }

        if (t == HubEventType.DEVICE_REMOVED) {
            DeviceRemovedEvent ev = (DeviceRemovedEvent) e;
            return DeviceRemovedEventAvro.newBuilder()
                    .setId(ev.getId())
                    .build();
        }

        if (t == HubEventType.SCENARIO_ADDED) {
            ScenarioAddedEvent ev = (ScenarioAddedEvent) e;
            return ScenarioAddedEventAvro.newBuilder()
                    .setName(ev.getName())
                    .setConditions(mapConditions(ev.getConditions()))
                    .setActions(mapActions(ev.getActions()))
                    .build();
        }

        if (t == HubEventType.SCENARIO_REMOVED) {
            ScenarioRemovedEvent ev = (ScenarioRemovedEvent) e;
            return ScenarioRemovedEventAvro.newBuilder()
                    .setName(ev.getName())
                    .build();
        }

        throw new IllegalArgumentException("Unsupported HubEventType: " + t);
    }

    private static List<DeviceActionAvro> mapActions(List<DeviceAction> src) {
        List<DeviceActionAvro> out = new ArrayList<>();
        if (src == null) return out;
        for (DeviceAction a : src) {
            if (a == null) continue;
            out.add(map(a));
        }
        return out;
    }

    private static List<ScenarioConditionAvro> mapConditions(List<ScenarioCondition> src) {
        List<ScenarioConditionAvro> out = new ArrayList<>();
        if (src == null) return out;
        for (ScenarioCondition c : src) {
            if (c == null) continue;
            out.add(map(c));
        }
        return out;
    }

    private static DeviceActionAvro map(DeviceAction a) {
        DeviceActionAvro.Builder b = DeviceActionAvro.newBuilder()
                .setSensorId(a.getSensorId())
                .setType(map(a.getType()));

        if (a.getValue() != null) b.setValue(a.getValue()); else b.setValue(null);
        return b.build();
    }

    private static ScenarioConditionAvro map(ScenarioCondition c) {
        ScenarioConditionAvro.Builder b = ScenarioConditionAvro.newBuilder()
                .setSensorId(c.getSensorId())
                .setType(map(c.getType()))
                .setOperation(map(c.getOperation()));
        if (c.getValue() != null) b.setValue(c.getValue()); else b.setValue(null);
        return b.build();
    }

    private static DeviceTypeAvro map(DeviceType v) {
        return DeviceTypeAvro.valueOf(v.name());
    }

    private static ActionTypeAvro map(ActionType v) {
        return ActionTypeAvro.valueOf(v.name());
    }

    private static ConditionTypeAvro map(ConditionType v) {
        return ConditionTypeAvro.valueOf(v.name());
    }

    private static ConditionOperationAvro map(ConditionOperation v) {
        return ConditionOperationAvro.valueOf(v.name());
    }
}
