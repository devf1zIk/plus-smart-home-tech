package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.service.ScenarioExecutionService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final ScenarioExecutionService scenarioExecutionService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.snapshots}")
    private String snapshotsTopic;

    public void start() {
        log.info("Запуск SnapshotProcessor. Подписка на топики: {} ", snapshotsTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshots-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.telemetry.serialization.SensorsSnapshotDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(snapshotsTopic));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    scenarioExecutionService.processSnapshot(record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor: ", e);
        }
    }
}