package ru.yandex.practicum.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
public class AggregationStart {

    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaConfigProperties kafkaConfig;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public AggregationStart(KafkaProducer<String, SensorsSnapshotAvro> producer,
                            KafkaConsumer<String, SensorEventAvro> consumer,
                            KafkaConfigProperties kafkaConfig) {
        this.producer = producer;
        this.consumer = consumer;
        this.kafkaConfig = kafkaConfig;
    }

    public void start() {
        final String telemetrySensors = kafkaConfig.getTopics().get("telemetry-sensors");
        final String telemetrySnapshots = kafkaConfig.getTopics().get("telemetry-snapshots");

        try {
            consumer.subscribe(Collections.singletonList(telemetrySensors));
            log.info("Подписка на топик: {}", telemetrySensors);

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));
                if (records.isEmpty()) continue;

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    updateState(event).ifPresent(snapshot -> {
                        try {
                            producer.send(new ProducerRecord<>(telemetrySnapshots, snapshot.getHubId(), snapshot));
                            log.info("Отправлен snapshot hubId={} в топик={}", snapshot.getHubId(), telemetrySnapshots);
                        } catch (Exception e) {
                            log.error("Ошибка при отправке snapshot", e);
                        }
                    });
                }

                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке событий Kafka", e);
        } finally {
            try {
                producer.flush();
                producer.close();
                consumer.close();
            } catch (Exception e) {
                log.error("Ошибка при закрытии ресурсов", e);
            }
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.getOrDefault(event.getHubId(),
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()))
                        .setSensorsState(new HashMap<>())
                        .build());

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        if (oldState != null
                && !oldState.getTimestamp().isBefore(event.getTimestamp())
                && oldState.getData().equals(event.getPayload())) {
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        snapshots.put(event.getHubId(), snapshot);

        return Optional.of(snapshot);
    }
}