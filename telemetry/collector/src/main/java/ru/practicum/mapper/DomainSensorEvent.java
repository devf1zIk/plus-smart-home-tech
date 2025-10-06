package ru.practicum.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainSensorEvent {
    public enum Type { MOTION, TEMPERATURE, LIGHT, CLIMATE, SWITCH, UNKNOWN }

    private String id;
    private String hubId;
    private Type type;

        // motion
    private Boolean motion;
    private Integer linkQuality;
    private Integer voltage;

        // temperature
    private Integer temperatureC;
    private Integer temperatureF;

        // light
    private Integer luminosity;

        // climate
    private Integer humidity;
    private Integer co2Level;

        // switch
    private Boolean switchState;
}
