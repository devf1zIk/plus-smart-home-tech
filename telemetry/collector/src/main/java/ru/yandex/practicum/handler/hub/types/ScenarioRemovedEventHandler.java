package ru.yandex.practicum.handler.hub.types;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.mapper.protoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final KafkaEventProducer kafkaProducer;
    private final protoMapper protoMapper;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        var scenario = event.getScenarioRemoved();
        System.out.printf("[Hub] ScenarioRemoved: name=%s, hub=%s%n",
                scenario.getName(), event.getHubId());

        var avroEvent = protoMapper.toAvro(event);
        String hubEventsTopic = "telemetry.hubs.v1";
        kafkaProducer.send(hubEventsTopic, event.getHubId(), Instant.now(),avroEvent);
    }
}