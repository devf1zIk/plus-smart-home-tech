package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scenarios", uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "hub_id", nullable = false)
    String hubId;

    @Column(nullable = false)
    String name;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<ScenarioCondition> conditions = new HashSet<>();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ScenarioAction> actions = new HashSet<>();
}