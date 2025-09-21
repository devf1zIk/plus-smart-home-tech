package ru.practicum.event.sensor.types;

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
public class LightSensorEvent extends SensorEvent {

    Integer linkQuality;
    Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
