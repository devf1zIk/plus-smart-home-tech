package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotInfoOrderToCalculateException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto createDelivery(NewDeliveryRequestDto request) {
        if (request.getFromAddress() == null || request.getToAddress() == null || request.getOrderId() == null) {
            throw new NoDeliveryFoundException("Запрос на доставку не был создан");
        }

        Delivery delivery = Delivery.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(request.getOrderId())
                .totalWeight(request.getTotalWeight())
                .totalVolume(request.getTotalVolume())
                .fragile(request.getFragile())
                .deliveryState(DeliveryState.CREATED)
                .fromAddress(deliveryMapper.toAddress(request.getFromAddress()))
                .toAddress(deliveryMapper.toAddress(request.getToAddress()))
                .build();

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryDto success(UUID orderId) {
        Delivery delivery = getDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.delivery(orderId);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public DeliveryDto failed(UUID orderId) {
        Delivery delivery = getDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.deliveryFailed(orderId);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public DeliveryDto pickOrder(UUID orderId) {
        Delivery delivery = getDeliveryByOrderIdOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder().orderId(orderId).deliveryId(delivery.getDeliveryId()).build());
        deliveryRepository.save(delivery);
        orderClient.assembly(orderId);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto order) {
        if (order == null || order.getDeliveryVolume() == null || order.getDeliveryWeight() == null) {
            throw new NotInfoOrderToCalculateException("Данные заказа не переданы");
        }

        Delivery delivery = getDeliveryByOrderIdOrThrow(order.getOrderId());
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        return getDeliveryCost(order, warehouseAddress, delivery);
    }

    private double getDeliveryCost(OrderDto order, AddressDto warehouseAddress, Delivery delivery) {
        double base = 5.0;
        double cost = base;

        if (warehouseAddress.getStreet().contains("ADDRESS_1")) {
            cost = base * 1 + base;
        } else if (warehouseAddress.getStreet().contains("ADDRESS_2")) {
            cost = base * 2 + base;
        }

        if (Boolean.TRUE.equals(order.getFragile())) {
            cost += cost * 0.2;
        }

        cost += order.getDeliveryWeight() * 0.3;
        cost += order.getDeliveryVolume() * 0.2;

        if (!warehouseAddress.getStreet().equalsIgnoreCase(delivery.getToAddress().getStreet())) {
            cost += cost * 0.2;
        }

        return Math.round(cost * 100.0) / 100.0;
    }

    private Delivery getDeliveryByOrderIdOrThrow(UUID orderId) {
        if (orderId == null) {
            throw new NoOrderFoundException("Идентификатор заказа не передан");
        }
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка для указанного заказа не найдена"));
    }
}
