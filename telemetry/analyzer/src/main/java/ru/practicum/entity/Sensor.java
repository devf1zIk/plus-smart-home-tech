package ru.practicum.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    String id;

    @Column(name = "hub_id", nullable = false)
    String hubId;
}