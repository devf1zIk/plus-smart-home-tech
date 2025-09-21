package ru.practicum;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.practicum.kafka.KafkaConfigProperties;
import ru.practicum.kafka.KafkaEventProducer;

@SpringBootApplication
@EnableConfigurationProperties({KafkaConfigProperties.class})
@Slf4j
public class CollectorApplication {

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    public static void main(String[] args) {
        SpringApplication.run(CollectorApplication.class, args);
    }

    @PreDestroy
    public void cleanup() {
        try {
            kafkaEventProducer.close();
        } catch (Exception e) {
            log.error("Error closing Kafka producer", e);
        }
    }
}