package ru.practicum.service.event;

import ru.practicum.event.hub.base.HubEvent;
import ru.practicum.event.sensor.base.SensorEvent;

public interface EventService {

    void publishSensorEvent(SensorEvent event);

    void publishHubEvent(HubEvent event);

}
