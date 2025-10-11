package ru.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Sensor {

    @Id
    @Column(name = "id")
    String id;

    @Column(name = "hub_id", nullable = false)
    String hubId;
}
