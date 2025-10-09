package ru.practicum.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ScenarioActionId implements Serializable {
    Long scenarioId;
    String sensorId;
    Long actionId;
}
