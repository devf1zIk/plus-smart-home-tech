package ru.practicum.kafka;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.EnumMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("kafka")
@Data
public class KafkaConfigProperties {
    private ProducerConfig producer;
    private ConsumerConfig consumer;
    private Map<String, String> topics;

    public enum TopicType {
        TELEMETRY_SENSORS, TELEMETRY_SNAPSHOTS;

        public static TopicType from(String type) {
            switch (type) {
                case "telemetry-sensors" -> {
                    return TopicType.TELEMETRY_SENSORS;
                }
                case "telemetry-snapshots" -> {
                    return TopicType.TELEMETRY_SNAPSHOTS;
                }
                default -> throw new RuntimeException("Топик не найден");

            }
        }
    }

    @Data
    public static class ConsumerConfig {
        private String bootstrapServers;
        private String groupId;
        private String clientId;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Data
    public static class ProducerConfig {
        private String bootstrapServers;
        private String keySerializer;
        private String valueSerializer;
    }

    @Getter
    public static class Topics {
        private final EnumMap<TopicType, String> topics = new EnumMap<>(TopicType.class);

        public Topics(Map<String, String> topics) {
            for (Map.Entry<String, String> entry : topics.entrySet()) {
                this.topics.put(TopicType.from(entry.getKey()), entry.getValue());
            }
        }
    }

    @Bean
    public EnumMap<TopicType, String> topics() {
        return new Topics(topics).getTopics();
    }

}
