package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.yandex.practicum.client")
public class InteractionApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(InteractionApiApplication.class, args);
    }
}
