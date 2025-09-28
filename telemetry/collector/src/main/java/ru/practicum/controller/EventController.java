package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.mapper.ProtoMapper;
import ru.practicum.service.sensor.SensorService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerOuterClass.Ack;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final ProtoMapper protoMapper;
    private final SensorService sensorService;

    @Override
    public void collectSensorEvent(SensorEventProto proto, StreamObserver<Ack> response) {
        try {
            sensorService.handle(protoMapper.toDomain(proto));
            response.onNext(ackOk("Sensor event received"));
            response.onCompleted();
        } catch (Exception e) {
            response.onError(asInternal(e));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto proto, StreamObserver<Ack> response) {
        try {
            log.debug("Hub event: hubId={}, payload={}", proto.getHubId(), proto.getPayloadCase());
            response.onNext(ackOk("hub received"));
            response.onCompleted();
        } catch (Exception e) {
            response.onError(asInternal(e));
        }
    }

    @Override
    public void ping(Empty request, StreamObserver<Ack> response) {
        response.onNext(ackOk("pong"));
        response.onCompleted();
    }

    private static Ack ackOk(String msg) {
        return Ack.newBuilder().setStatus("OK").setMessage(msg).build();
    }

    private static StatusRuntimeException asInternal(Exception e) {
        return new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage()).withCause(e));
    }
}
