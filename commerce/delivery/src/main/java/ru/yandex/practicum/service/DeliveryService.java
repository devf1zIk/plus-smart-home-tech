package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.dto.order.OrderDto;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(NewDeliveryRequestDto request);

    DeliveryDto success(UUID orderId);

    DeliveryDto failed(UUID orderId);

    DeliveryDto pickOrder(UUID orderId);

    Double calculateDeliveryCost(OrderDto order);
}
