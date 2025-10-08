package ru.practicum.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class KafkaEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final ObjectMapper objectMapper;

    public KafkaEventProducer(KafkaConfigProperties kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientIdConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerValueSerializer());
        this.producer = new KafkaProducer<>(props);
        this.objectMapper = new ObjectMapper();
    }

    public void send(String topic, String key, Instant timestamp, SpecificRecordBase value) {
        long ts = timestamp.toEpochMilli();
        String valueType = value != null ? value.getClass().getSimpleName() : "null";
        String payloadType = getPayloadType(value);

        Map<String, Object> preSendLog = new HashMap<>();
        preSendLog.put("class", this.getClass().getSimpleName());
        preSendLog.put("method", "send");
        preSendLog.put("event", "kafka_pre_send");
        preSendLog.put("topic", topic);
        preSendLog.put("key", key);
        preSendLog.put("value_type", valueType);
        preSendLog.put("payload_type", payloadType);
        preSendLog.put("timestamp", ts);

        try {
            String preSendJson = objectMapper.writeValueAsString(preSendLog);
            log.info(preSendJson);
        } catch (Exception e) {
            log.error("Failed to serialize pre-send log", e);
        }

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                ts,
                key,
                value
        );

        producer.send(record, (metadata, exception) -> {
            String className = this.getClass().getSimpleName();
            String methodName = "send";

            if (exception != null) {
                Map<String, Object> logData = new HashMap<>();
                logData.put("class", className);
                logData.put("method", methodName);
                logData.put("event", "kafka_send_failed");
                logData.put("topic", topic);
                logData.put("key", key);
                logData.put("value_type", valueType);
                logData.put("payload_type", payloadType);
                logData.put("timestamp", ts);
                logData.put("error", exception.toString());

                try {
                    String jsonLog = objectMapper.writeValueAsString(logData);
                    log.error(jsonLog);
                } catch (Exception e) {
                    log.error("Failed to serialize log data to JSON", e);
                }
            } else {
                Map<String, Object> logData = new HashMap<>();
                logData.put("class", className);
                logData.put("method", methodName);
                logData.put("event", "kafka_send_success");
                logData.put("topic", metadata.topic());
                logData.put("partition", metadata.partition());
                logData.put("offset", metadata.offset());
                logData.put("key", key);
                logData.put("value_type", valueType);
                logData.put("payload_type", payloadType);
                logData.put("producer_timestamp", ts);
                logData.put("kafka_timestamp", metadata.timestamp());

                try {
                    String jsonLog = objectMapper.writeValueAsString(logData);
                    log.info(jsonLog);
                } catch (Exception e) {
                    log.error("Failed to serialize log data to JSON", e);
                }
            }
        });
    }

    private String getPayloadType(SpecificRecordBase value) {
        if (value == null) {
            return "null";
        }

        try {
            Object payload = value.get("payload");
            if (payload != null) {
                return payload.getClass().getSimpleName();
            }

            String[] possiblePayloadFields = {"event", "sensorEvent", "hubEvent", "data"};
            for (String fieldName : possiblePayloadFields) {
                try {
                    Object fieldValue = value.get(fieldName);
                    if (fieldValue != null) {
                        return fieldValue.getClass().getSimpleName();
                    }
                } catch (Exception e) {
                }
            }

            return value.getSchema().getName();

        } catch (Exception e) {
            return "error_getting_payload_type";
        }
    }
}