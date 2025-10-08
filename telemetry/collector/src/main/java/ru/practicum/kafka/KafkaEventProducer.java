package ru.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class KafkaEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public KafkaEventProducer(KafkaConfigProperties kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientIdConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerValueSerializer());
        this.producer = new KafkaProducer<>(props);
    }

    public void send(String topic, String key, Instant timestamp, SpecificRecordBase value) {
        long ts = timestamp.toEpochMilli();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                ts,
                key,
                value
        );

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("[{}#{}] Kafka send failed: topic={} key={} ts={} err={}",
                        this.getClass().getSimpleName(),
                        "send",
                        topic, key, ts, exception.toString());
            } else {
                log.debug("Kafka sent: topic={} partition={} offset={} ts={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
            }
        });
    }
}
