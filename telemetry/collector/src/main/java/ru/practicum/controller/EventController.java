package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.handler.hub.HubEventHandler;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorHandlerSet,
                           Set<HubEventHandler> hubHandlerSet) {

        Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorHandlers = new HashMap<>();
        Map<HubEventProto.PayloadCase, HubEventHandler> hubHandlers = new HashMap<>();

        if (sensorHandlerSet != null) {
            for (SensorEventHandler handler : sensorHandlerSet) {
                SensorEventProto.PayloadCase type = handler.getMessageType();
                sensorHandlers.put(type, handler);
            }
        }

        if (hubHandlerSet != null) {
            for (HubEventHandler handler : hubHandlerSet) {
                HubEventProto.PayloadCase type = handler.getMessageType();
                hubHandlers.put(type, handler);
            }
        }
        this.sensorEventHandlers = sensorHandlers;
        this.hubEventHandlers = hubHandlers;
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEventProto.PayloadCase type = request.getPayloadCase();
            HubEventHandler handler = hubEventHandlers.get(type);

            if (handler != null) {
                handler.handle(request);
            } else {
                System.out.println("⚠ Неизвестный тип события от хаба: " + type);
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)
            ));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventProto.PayloadCase type = request.getPayloadCase();
            SensorEventHandler handler = sensorEventHandlers.get(type);

            if (handler != null) {
                handler.handle(request);
            } else {
                System.out.println("⚠ Неизвестный тип события от датчика: " + type);
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)
            ));
        }
    }
}
