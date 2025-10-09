package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.processor.HubEventProcessor;
import ru.practicum.processor.SnapshotProcessor;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);

        HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
        SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventProcessor-Thread");
        hubEventsThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал завершения работы. Ожидаем завершения потоков...");
            try {
                hubEventsThread.interrupt();
                hubEventsThread.join(2000);
            } catch (InterruptedException e) {
                log.warn("Принудительное завершение потока HubEventProcessor", e);
                Thread.currentThread().interrupt();
            }
            log.info("Analyzer корректно завершил работу.");
        }));

        snapshotProcessor.start();
    }
}
