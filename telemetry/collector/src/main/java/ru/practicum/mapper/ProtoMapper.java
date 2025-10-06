package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.event.sensor.types.*;
import ru.yandex.practicum.grpc.telemetry.event.*;

@Component
public class ProtoMapper {

    public DomainSensorEvent toDomain(SensorEventProto proto) {
        DomainSensorEvent out = new DomainSensorEvent();

        out.setId(proto.getId());
        out.setHubId(proto.getHubId());

        switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT:
                MotionSensorProto m = proto.getMotionSensorEvent();
                out.setType(DomainSensorEvent.Type.MOTION);
                out.setMotion(m.getMotion());
                out.setLinkQuality(m.getLinkQuality());
                out.setVoltage(m.getVoltage());
                break;

            case TEMPERATURE_SENSOR_EVENT:
                TemperatureSensorProto t = proto.getTemperatureSensorEvent();
                out.setType(DomainSensorEvent.Type.TEMPERATURE);
                out.setTemperatureC(t.getTemperatureC());
                out.setTemperatureF(t.getTemperatureF());
                break;

            case LIGHT_SENSOR_EVENT:
                LightSensorProto l = proto.getLightSensorEvent();
                out.setType(DomainSensorEvent.Type.LIGHT);
                out.setLuminosity(l.getLuminosity());
                out.setLinkQuality(l.getLinkQuality());
                break;

            case CLIMATE_SENSOR_EVENT:
                ClimateSensorProto c = proto.getClimateSensorEvent();
                out.setType(DomainSensorEvent.Type.CLIMATE);
                out.setTemperatureC(c.getTemperatureC());
                out.setHumidity(c.getHumidity());
                out.setCo2Level(c.getCo2Level());
                break;

            case SWITCH_SENSOR_EVENT:
                SwitchSensorProto s = proto.getSwitchSensorEvent();
                out.setType(DomainSensorEvent.Type.SWITCH);
                out.setSwitchState(s.getState());
                break;

            case PAYLOAD_NOT_SET:
            default:
                out.setType(DomainSensorEvent.Type.UNKNOWN);
                break;
        }
        return out;
    }
}
