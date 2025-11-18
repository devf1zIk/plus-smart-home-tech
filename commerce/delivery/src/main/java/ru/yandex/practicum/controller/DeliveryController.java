package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {

    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto create(@RequestBody NewDeliveryRequestDto requestDto) {
        return deliveryService.createDelivery(requestDto);
    }

    @Override
    @PostMapping("/cost")
    public Double cost(@RequestBody OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }

    @Override
    @PostMapping("/picked")
    public DeliveryDto picked(@RequestBody UUID orderId) {
        return deliveryService.pickOrder(orderId);
    }

    @Override
    @PostMapping("/successful")
    public DeliveryDto successful(@RequestBody UUID orderId) {
        return deliveryService.success(orderId);
    }

    @Override
    @PostMapping("/failed")
    public DeliveryDto failed(@RequestBody UUID orderId) {
        return deliveryService.failed(orderId);
    }
}
