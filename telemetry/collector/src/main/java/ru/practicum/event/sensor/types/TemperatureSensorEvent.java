package ru.practicum.event.sensor.types;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.sensor.base.SensorEvent;
import ru.practicum.event.sensor.enums.SensorEventType;

@Setter
@Getter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    Integer temperatureC;

    @NotNull
    Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
