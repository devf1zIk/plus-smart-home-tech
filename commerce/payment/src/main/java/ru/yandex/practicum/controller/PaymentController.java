package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {

    private final PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto orderDto) {
        return paymentService.createPayment(orderDto);
    }

    @Override
    @PostMapping("/totalCost")
    public BigDecimal totalCost(OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    @PostMapping("/refund")
    public void refund(@RequestBody UUID paymentId) {
        paymentService.successPayment(paymentId);
    }

    @PostMapping("/productCost")
    @Override
    public BigDecimal productCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @PostMapping("/failed")
    @Override
    public void failed(@RequestBody UUID paymentId) {
        paymentService.failedPayment(paymentId);
    }
}
