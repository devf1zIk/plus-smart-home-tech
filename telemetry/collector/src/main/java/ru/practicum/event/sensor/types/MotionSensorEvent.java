package ru.practicum.event.sensor.types;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.sensor.base.SensorEvent;
import ru.practicum.event.sensor.enums.SensorEventType;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MotionSensorEvent extends SensorEvent {
    @NotNull
    Integer linkQuality;

    @NotNull
    Boolean motion;

    @NotNull
    Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
