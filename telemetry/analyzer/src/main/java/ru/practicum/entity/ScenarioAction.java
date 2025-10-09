package ru.practicum.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioAction {

    @EmbeddedId
    ScenarioActionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    Action action;
}
