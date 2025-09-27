package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.event.sensor.base.SensorEvent;
import ru.practicum.event.sensor.types.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import java.time.Instant;

@Component
public class ProtoMapper {

    public SensorEvent toDomain(SensorEventProto proto) {
        if (proto == null) {
            throw new IllegalArgumentException("SensorEventProto is null");
        }

        final Instant ts = Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );

        if (proto.hasMotion()) {
            var p = proto.getMotion();
            MotionSensorEvent e = new MotionSensorEvent();
            setBase(e, proto, ts);
            e.setLinkQuality(p.getLinkQuality());
            e.setMotion(p.getMotion());
            e.setVoltage(p.getVoltage());
            return e;

        } else if (proto.hasTemperature()) {
            var p = proto.getTemperature();
            TemperatureSensorEvent e = new TemperatureSensorEvent();
            setBase(e, proto, ts);
            e.setTemperatureC(p.getTemperatureC());
            e.setTemperatureF(p.getTemperatureF());
            return e;

        } else if (proto.hasLight()) {
            var p = proto.getLight();
            LightSensorEvent e = new LightSensorEvent();
            setBase(e, proto, ts);
            e.setLinkQuality(p.getLinkQuality());
            e.setLuminosity(p.getLuminosity());
            return e;

        } else if (proto.hasClimate()) {
            var p = proto.getClimate();
            ClimateSensorEvent e = new ClimateSensorEvent();
            setBase(e, proto, ts);
            e.setTemperatureC(p.getTemperatureC());
            e.setHumidity(p.getHumidity());
            e.setCo2Level(p.getCo2Level());
            return e;

        } else if (proto.hasSw()) {
            var p = proto.getSw();
            SwitchSensorEvent e = new SwitchSensorEvent();
            setBase(e, proto, ts);
            e.setState(p.getState());
            return e;
        }

        throw new IllegalArgumentException("Unsupported payload in SensorEventProto: oneof is empty");
    }

    private static void setBase(SensorEvent target, SensorEventProto proto, Instant ts) {
        target.setId(proto.getId());
        target.setHubId(proto.getHubId());
        target.setTimestamp(ts);
    }
}