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
import ru.yandex.practicum.exception.NotInfoOrderToCalculateException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

        if (request.getOrderId() == null ||
                request.getFromAddress() == null ||
                request.getToAddress() == null) {
            throw new NoDeliveryFoundException("Запрос на доставку имеет пустые поля");
        }

        Delivery delivery = deliveryMapper.toModel(request);

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(saved);
    }

    @Override
    public DeliveryDto success(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.delivery(orderId);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public DeliveryDto failed(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.deliveryFailed(orderId);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public DeliveryDto pickOrder(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        warehouseClient.shippedToDelivery(
                ShippedToDeliveryRequest.builder()
                        .orderId(orderId)
                        .deliveryId(delivery.getDeliveryId())
                        .build()
        );
        orderClient.assembly(orderId);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto order) {

        if (order == null ||
                order.getDeliveryWeight() == null ||
                order.getDeliveryVolume() == null) {
            throw new NotInfoOrderToCalculateException("Недостаточно данных доставки");
        }

        Delivery delivery = deliveryRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        BigDecimal cost = BigDecimal.valueOf(5.0);

        if (warehouseAddress.getStreet() != null &&
                warehouseAddress.getStreet().contains("ADDRESS_1")) {
            cost = BigDecimal.valueOf(5.0).multiply(BigDecimal.ONE).add(BigDecimal.valueOf(5.0));
        }

        if (warehouseAddress.getStreet() != null &&
                warehouseAddress.getStreet().contains("ADDRESS_2")) {
            cost = BigDecimal.valueOf(5.0).multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(5.0));
        }

        if (Boolean.TRUE.equals(order.getFragile())) {
            cost = cost.add(cost.multiply(BigDecimal.valueOf(0.2)));
        }

        cost = cost.add(order.getDeliveryWeight().multiply(BigDecimal.valueOf(0.3)));
        cost = cost.add(order.getDeliveryVolume().multiply(BigDecimal.valueOf(0.2)));
        if (!warehouseAddress.getStreet().equalsIgnoreCase(delivery.getToAddress().getStreet())) {
            cost = cost.add(cost.multiply(BigDecimal.valueOf(0.2)));
        }
        return cost.setScale(2, RoundingMode.HALF_UP);
    }
}