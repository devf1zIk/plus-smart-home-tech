package ru.practicum.event.sensor.types;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.sensor.base.SensorEvent;
import ru.practicum.event.sensor.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull
    Integer temperatureC;

    @NotNull
    Integer humidity;

    @NotNull
    Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
