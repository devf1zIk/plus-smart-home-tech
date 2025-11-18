package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {

    private final OrderService orderService;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(@RequestParam String username) {
        return orderService.getUserOrders(username);
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest request, @RequestParam String username) {
        return orderService.createOrder(request, username);
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(@Valid @RequestBody ProductReturnRequest request) {
        return orderService.returnProducts(request);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payment(@RequestBody UUID orderId) {
        return orderService.payment(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        return orderService.paymentFailed(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody UUID orderId) {
        return orderService.delivery(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto complete(@RequestBody UUID orderId) {
        return orderService.completed(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody UUID orderId) {
        return orderService.calculateTotal(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        return orderService.calculateDelivery(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody UUID orderId) {
        return orderService.assembly(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
