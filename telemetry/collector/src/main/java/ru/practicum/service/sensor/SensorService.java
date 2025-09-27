package ru.practicum.service.sensor;

import ru.practicum.event.sensor.base.SensorEvent;

public interface SensorService {

    void handle(SensorEvent event);
}
