package ru.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
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
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final ProtoMapper mapper;

    private final SensorService service;

    public CollectorController(ProtoMapper mapper, SensorService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Ack> response) {
        try {
            service.handle(mapper.toDomain(request));
            response.onNext(ackOk("sensor received"));
            response.onCompleted();
        } catch (Exception e) {
            response.onError(asInternal(e));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Ack> response) {
        try {
            log.debug("Hub event: hubId={}, payload={}", request.getHubId(), request.getPayloadCase());
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
