package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getUserOrders(String username);

    OrderDto createOrder(CreateNewOrderRequest request, String username);

    OrderDto returnProducts(ProductReturnRequest request);

    OrderDto calculateTotal(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto payment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto delivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto completed(UUID orderId);

    OrderDto assembly(UUID orderId);

    OrderDto assemblyFailed(UUID orderId);
}
