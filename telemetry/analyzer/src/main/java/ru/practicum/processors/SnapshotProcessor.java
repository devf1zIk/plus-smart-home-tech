package ru.practicum.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.KafkaConfigProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.practicum.services.ScenarioAnalysisService;
import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final KafkaConfigProperties kafkaProperties;
    private final ScenarioAnalysisService scenarioAnalysisService;

    @Override
    public void run() {
        log.info("Starting snapshot processor...");
        try {
            snapshotConsumer.subscribe(Collections.singletonList(kafkaProperties.getTopics().getSensorSnapshots()));

            while (true) {
                try {
                    ConsumerRecords<String, SensorsSnapshotAvro> records =
                            snapshotConsumer.poll(Duration.ofMillis(kafkaProperties.getConsumer().getConsumeTimeout()));

                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                        log.info("Received snapshot for hub: {}", record.key());
                        processSnapshot(record.value());
                    }

                    if (!records.isEmpty()) {
                        snapshotConsumer.commitSync();
                    }
                } catch (WakeupException e) {
                    log.info("Snapshot processor received wakeup signal, shutting down...");
                    break;
                } catch (Exception e) {
                    log.error("Error processing snapshot", e);
                }
            }
        } catch (Exception e) {
            log.error("Error in snapshot processor", e);
        } finally {
            try {
                snapshotConsumer.close();
                log.info("Snapshot consumer closed successfully");
            } catch (Exception e) {
                log.warn("Error closing snapshot consumer", e);
            }
        }
    }

    private void processSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Processing snapshot for hub: {}", snapshot.getHubId());

        try {
            scenarioAnalysisService.analyzeSnapshot(snapshot);
        } catch (Exception e) {
            log.error("Error while parsing scenarios for snapshot", e);
        }
    }
}
