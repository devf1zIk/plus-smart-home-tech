package ru.practicum.kafka;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.util.Map;
import java.util.Properties;

@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaConfigProperties {

    private ProducerSettings producer;
    private ConsumerSettings consumer;
    private Map<String, String> topics;

    @Data
    public static class ProducerSettings {
        private String bootstrapServers;
        private String keySerializer;
        private String valueSerializer;
    }

    @Data
    public static class ConsumerSettings {
        private String bootstrapServers;
        private String groupId;
        private String clientId;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Bean
    public KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer.getValueSerializer());
        return new KafkaProducer<>(config);
    }

    @Bean
    public KafkaConsumer<String, SensorEventAvro> kafkaConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumer.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumer.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumer.getValueDeserializer());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroupId());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, consumer.getClientId());
        return new KafkaConsumer<>(config);
    }
}
