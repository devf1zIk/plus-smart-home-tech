package ru.practicum.event.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.hub.base.HubEvent;
import ru.practicum.event.hub.enums.HubEventType;
import ru.practicum.event.hub.device.DeviceAction;

import java.util.List;

@Setter
@Getter
@ToString(callSuper=true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3, max = 2147483647)
    String name;

    @NotNull
    List<ScenarioCondition> conditions;

    @NotNull
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
