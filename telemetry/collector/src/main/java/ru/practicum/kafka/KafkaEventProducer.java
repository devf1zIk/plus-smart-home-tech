package ru.practicum.kafka;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public void send(String topic, String key, SpecificRecord value) {
        send(topic, key, null, value);
    }

    public void send(String topic, String key, Instant timestamp, SpecificRecord value) {
        long ts = (timestamp != null ? timestamp : Instant.now()).toEpochMilli();
        logAvroDetails("BEFORE SERIALIZATION", value);
        byte[] valueBytes = avroToBytes(value);

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

    private byte[] avroToBytes(SpecificRecord record) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<SpecificRecord> writer = new org.apache.avro.specific.SpecificDatumWriter<>(record.getSchema());

            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(record, encoder);
            encoder.flush();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Avro record to bytes", e);
        }
    }

    private void logAvroDetails(String stage, SpecificRecord record) {
        try {
            log.info("=== AVRO DEBUG {} ===", stage);
            log.info("Class: {}", record.getClass().getName());
            log.info("Schema name: {}", record.getSchema().getFullName());
            log.info("Schema: {}", record.getSchema().toString(true));

            record.getSchema().getFields().forEach(field -> {
                try {
                    Object value = record.get(field.pos());
                    log.info("Field [{}] (pos: {}): type={}, value={}",
                            field.name(), field.pos(), field.schema().getType(), value);

                    if (field.schema().getType() == org.apache.avro.Schema.Type.UNION) {
                        log.info("  Union types: {}", field.schema().getTypes());
                    }
                } catch (Exception e) {
                    log.warn("Failed to get field {}: {}", field.name(), e.getMessage());
                }
            });

            log.info("=== END AVRO DEBUG ===");
        } catch (Exception e) {
            log.error("Failed to log Avro details: {}", e.getMessage());
        }
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