package ru.practicum.service.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.event.sensor.base.SensorEvent;
import ru.practicum.event.sensor.types.*;

@Service
public class SensorServiceImpl implements SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorServiceImpl.class);

    @Override
    public void handle(SensorEvent event) {
        if (event == null) {
            log.warn("Skip null event");
            return;
        }

        try {
            if (event instanceof MotionSensorEvent) {
                handleMotion((MotionSensorEvent) event);
            } else if (event instanceof TemperatureSensorEvent) {
                handleTemperature((TemperatureSensorEvent) event);
            } else if (event instanceof LightSensorEvent) {
                handleLight((LightSensorEvent) event);
            } else if (event instanceof ClimateSensorEvent) {
                handleClimate((ClimateSensorEvent) event);
            } else if (event instanceof SwitchSensorEvent) {
                handleSwitch((SwitchSensorEvent) event);
            } else {
                log.error("Unknown sensor event type: {}", event.getClass().getName());
                throw new IllegalArgumentException("Unknown sensor event type: " + event.getClass());
            }
        } catch (Exception e) {
            // чтобы одно битое событие не валило поток
            log.error("Failed to handle event {}: {}", event.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    private void handleMotion(MotionSensorEvent e) {
        log.info("Motion: {}", e);
    }
    private void handleTemperature(TemperatureSensorEvent e) {
        log.info("Temperature: {}", e);
    } private void handleLight(LightSensorEvent e) {
        log.info("Light: {}", e);
    } private void handleClimate(ClimateSensorEvent e) {
        log.info("Climate: {}", e);
    } private void handleSwitch(SwitchSensorEvent e) {
        log.info("Switch: {}", e);
    }
}
