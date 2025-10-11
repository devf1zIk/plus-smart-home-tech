package ru.practicum;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.practicum.processors.HubEventProcessor;
import ru.practicum.processors.SnapshotProcessor;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class AnalyzerApplication implements CommandLineRunner {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    private Thread hubEventsThread;
    private Thread snapshotThread;

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Analyzer application...");

        hubEventsThread = new Thread(hubEventProcessor, "hub-event-processor");
        hubEventsThread.setDaemon(true);
        hubEventsThread.start();

        snapshotThread = new Thread(snapshotProcessor, "snapshot-processor");
        snapshotThread.setDaemon(true);
        snapshotThread.start();

        log.info("Kafka processors started successfully");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.info("Analyzer application interrupted");
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Analyzer application...");

        if (hubEventsThread != null && hubEventsThread.isAlive()) {
            log.info("Interrupting hub-events thread");
            hubEventsThread.interrupt();
        }

        if (snapshotThread != null && snapshotThread.isAlive()) {
            log.info("Interrupting snapshot thread");
            snapshotThread.interrupt();
        }
    }
}