package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.entity.Sensor;
import ru.practicum.repository.ScenarioRepository;
import ru.practicum.repository.SensorRepository;
import ru.practicum.service.ScenarioService;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final ScenarioService scenarioService;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.hub-events}")
    private String hubEventsTopic;

    @Override
    public void run() {
        log.info("Запуск HubEventProcessor. Подписка на топик: {}", hubEventsTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-hub-events-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.telemetry.serialization.HubEventDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(hubEventsTopic));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processEvent(record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в HubEventProcessor: ", e);
        }
    }

    private void processEvent(HubEventAvro event) {
        log.debug("Получено событие HubEventAvro: {}", event);

        switch (event.getPayload().getClass().getSimpleName()) {
            case "DeviceAddedEventAvro" -> handleDeviceAdded(event);
            case "DeviceRemovedEventAvro" -> handleDeviceRemoved(event);
            case "ScenarioAddedEventAvro" -> handleScenarioAdded(event);
            case "ScenarioRemovedEventAvro" -> handleScenarioRemoved(event);
            default -> log.warn("Неизвестный тип события: {}", event.getPayload().getClass());
        }
    }

    private void handleDeviceAdded(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();
        String sensorId = payload.getId();
        String hubId = event.getHubId();

        sensorRepository.findById(sensorId).ifPresentOrElse(
                s -> log.debug("Сенсор {} уже существует в хабе {}", sensorId, hubId),
                () -> {
                    sensorRepository.save(Sensor.builder()
                            .id(sensorId)
                            .hubId(hubId)
                            .build());
                    log.info("Добавлен новый сенсор {} в хаб {}", sensorId, hubId);
                });
    }

    private void handleDeviceRemoved(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();
        String sensorId = payload.getId();

        if (sensorRepository.existsById(sensorId)) {
            sensorRepository.deleteById(sensorId);
            log.info("Удалён сенсор {}", sensorId);
        } else {
            log.debug("Попытка удалить несуществующий сенсор {}", sensorId);
        }
    }

    private void handleScenarioAdded(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();

        try {
            scenarioService.upsertScenarioFromEvent(payload, hubId);
            log.info("Обработано ScenarioAdded для hub={}, name={}", hubId, payload.getName());
        } catch (Exception e) {
            log.error("Ошибка при обработке ScenarioAdded для hub={}, name={}: {}", hubId, payload.getName(), e.toString(), e);
        }
    }

    private void handleScenarioRemoved(HubEventAvro event) {
        ScenarioRemovedEventAvro payload = (ScenarioRemovedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String name = payload.getName();

        scenarioRepository.findByHubIdAndName(hubId, name).ifPresentOrElse(
                s -> {
                    scenarioRepository.delete(s);
                    log.info("Удалён сценарий {} из хаба {}", name, hubId);
                },
                () -> log.debug("Сценарий {} для хаба {} не найден", name, hubId)
        );
    }
}