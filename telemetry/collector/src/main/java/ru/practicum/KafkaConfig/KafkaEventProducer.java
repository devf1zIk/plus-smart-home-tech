package ru.practicum.KafkaConfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Properties;

@Component
@Slf4j
public class KafkaEventProducer implements AutoCloseable {

    private final KafkaProducer<String, Object> producer;

    public KafkaEventProducer(KafkaConfigProperties kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientIdConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerValueSerializer());
        this.producer = new KafkaProducer<>(props);
    }

    public void send(String topic, String key, Object value) {
        try {
            producer.send(new ProducerRecord<>(topic, key, value), (metadata, exception) -> {
                if (exception != null) {
                    log.error("Failed to send message to topic {}: {}", topic, exception.getMessage());
                } else {
                    log.debug("Message sent successfully to topic {}: partition={}, offset={}",
                            topic, metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Error sending message to topic {}: {}", topic, e.getMessage());
        }
    }

    public void send(String topic, Object value) {
        send(topic, null, value);
    }

    @Override
    public void close() {
        if (producer != null) {
            try {
                producer.flush();
                producer.close(Duration.ofSeconds(10));
            } catch (Exception e) {
                log.error("Error closing Kafka producer", e);
            }
        }
    }
}
