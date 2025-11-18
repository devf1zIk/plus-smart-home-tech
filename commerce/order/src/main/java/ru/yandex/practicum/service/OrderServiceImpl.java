package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final WarehouseClient warehouseClient;

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(String username) {
        return orderRepository.findAllByUsername(username).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public OrderDto createOrder(CreateNewOrderRequest request, String username) {
        Order order = Order.builder()
                .order_id(UUID.randomUUID())
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .username(username)
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .fragile(false)
                .totalPrice(BigDecimal.ZERO)
                .deliveryPrice(BigDecimal.ZERO)
                .productPrice(BigDecimal.ZERO)
                .build();
        orderRepository.save(order);

        AssemblyProductsForOrderRequest assemblyRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrder_id())
                .products(request.getShoppingCart().getProducts())
                .build();

        BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(assemblyRequest);

        NewDeliveryRequestDto deliveryRequest = NewDeliveryRequestDto.builder()
                .orderId(order.getOrder_id())
                .toAddress(request.getDeliveryAddress())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .totalWeight(booked.getDeliveryWeight())
                .totalVolume(booked.getDeliveryVolume())
                .fragile(booked.isFragile())
                .build();

        DeliveryDto delivery = deliveryClient.create(deliveryRequest);
        order.setState(OrderState.ASSEMBLED);
        order.setDeliveryId(delivery.getDeliveryId());

        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto returnProducts(ProductReturnRequest request) {
        Order order = getOrThrow(request.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);
        warehouseClient.returnProduct(request.getProducts());
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrThrow(orderId);

        BigDecimal productCost = paymentClient.productCost(orderMapper.toDto(order));
        order.setProductPrice(productCost);

        BigDecimal deliveryCost = deliveryClient.cost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);

        BigDecimal totalCost = paymentClient.totalCost(orderMapper.toDto(order));
        order.setTotalPrice(totalCost);

        order.setState(OrderState.ON_PAYMENT);

        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto calculateDelivery(UUID orderId) {
        Order order = getOrThrow(orderId);
        BigDecimal deliveryCost = deliveryClient.cost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto payment(UUID orderId) {
        Order order = getOrThrow(orderId);
        paymentClient.createPayment(orderMapper.toDto(order));
        order.setState(OrderState.ON_PAYMENT);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto paymentSuccess(UUID orderId) {
        Order order = getOrThrow(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }


    public OrderDto paymentFailed(UUID orderId) {
        return updateState(orderId, OrderState.PAYMENT_FAILED);
    }

    public OrderDto delivery(UUID orderId) {
        return updateState(orderId, OrderState.DELIVERED);
    }

    public OrderDto deliveryFailed(UUID orderId) {
        return updateState(orderId, OrderState.DELIVERY_FAILED);
    }

    public OrderDto completed(UUID orderId) {
        return updateState(orderId, OrderState.COMPLETED);
    }

    public OrderDto assembly(UUID orderId) {
        return updateState(orderId, OrderState.ASSEMBLED);
    }

    public OrderDto assemblyFailed(UUID orderId) {
        return updateState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    private Order getOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoOrderFoundException(
                        "Заказ не найден: " + id
                ));
    }

    private OrderDto updateState(UUID orderId, OrderState newState) {
        Order order = getOrThrow(orderId);
        order.setState(newState);
        return orderMapper.toDto(orderRepository.save(order));
    }
}