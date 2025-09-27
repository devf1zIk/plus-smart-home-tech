package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.hub.base.HubEvent;
import ru.practicum.service.event.EventService;
import ru.practicum.event.sensor.base.SensorEvent;

@RestController
@Slf4j
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping(path = "/sensors", consumes = "application/json")
    public ResponseEntity<Void> collectSensor(@Valid @RequestBody SensorEvent event) {
        log.debug("Incoming /events/sensors: {}", event);
        eventService.publishSensorEvent(event);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/hubs", consumes = "application/json")
    public ResponseEntity<Void> collectHub(@Valid @RequestBody HubEvent event) {
        log.debug("Incoming /events/hubs: {}", event);
        eventService.publishHubEvent(event);
        return ResponseEntity.accepted().build();
    }
}
