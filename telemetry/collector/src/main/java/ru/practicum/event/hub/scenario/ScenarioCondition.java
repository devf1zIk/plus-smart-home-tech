package ru.practicum.event.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.hub.enums.ConditionOperation;
import ru.practicum.event.hub.enums.ConditionType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {

    @NotBlank
    String sensorId;

    @NotNull
    ConditionType type;

    @NotNull
    ConditionOperation operation;

    Integer value;
}
