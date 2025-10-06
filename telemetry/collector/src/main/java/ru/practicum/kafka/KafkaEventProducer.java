package ru.practicum.kafka;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class KafkaEventProducer implements AutoCloseable {

    private final KafkaProducer<String, byte[]> producer;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public KafkaEventProducer(KafkaConfigProperties kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientIdConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerValueSerializer());
        this.producer = new KafkaProducer<>(props);
    }

    public void send(String topic, String key, Instant timestamp, com.google.protobuf.Message value) {
        long ts = (timestamp != null ? timestamp : Instant.now()).toEpochMilli();

        byte[] valueBytes = value.toByteArray();

        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                topic,
                null,
                ts,
                key,
                valueBytes
        );

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Kafka send failed: topic={} key={} ts={} err={}",
                        topic, key, ts, exception.toString());
            } else {
                log.info("✅ Kafka sent: topic={} partition={} offset={} ts={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
            }
        });
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                producer.close(Duration.ofSeconds(10));
                log.info("Kafka producer closed");
            } catch (Exception e) {
                log.warn("Failed to close Kafka producer cleanly", e);
            }
        }
    }

    @PreDestroy
    void onShutdown() {
        close();
    }
}
