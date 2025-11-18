package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.PaymentNotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        validateOrder(orderDto);

        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal tax = productCost.multiply(BigDecimal.valueOf(0.1));
        BigDecimal delivery = orderDto.getDeliveryPrice();
        BigDecimal total = productCost.add(tax).add(delivery);

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .orderId(orderDto.getOrderId())
                .productTotal(productCost)
                .deliveryTotal(delivery)
                .feeTotal(tax)
                .totalPayment(total)
                .state(PaymentState.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);

        log.info("Создан платёж {} для заказа {}", saved.getPaymentId(), saved.getOrderId());

        return paymentMapper.toDto(saved);
    }


    public BigDecimal calculateProductCost(OrderDto orderDto) {
        validateOrder(orderDto);

        return orderDto.getProducts().entrySet().stream()
                .map(entry -> {
                    BigDecimal price = shoppingStoreClient.getProductById(entry.getKey())
                            .getPrice();
                    Long quantity = entry.getValue();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        validateOrder(orderDto);

        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal tax = productCost.multiply(BigDecimal.valueOf(0.1));
        BigDecimal delivery = orderDto.getDeliveryPrice();

        return productCost.add(tax).add(delivery);
    }

    @Override
    @Transactional
    public void successPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж не найден: " + paymentId));

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.payment(payment.getOrderId());
        log.info("Платёж {} отмечен как УСПЕШНЫЙ", paymentId);
    }

    @Override
    @Transactional
    public void failedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж не найден: " + paymentId));

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
        log.info("Платёж {} отмечен как ОТКЛОНЁН", paymentId);
    }

    private void validateOrder(OrderDto orderDto) {
        if (orderDto == null
                || orderDto.getProducts() == null
                || orderDto.getProducts().isEmpty()
                || orderDto.getDeliveryPrice() == null) {

            throw new NotEnoughInfoInOrderToCalculateException(
                    "Недостаточно данных для расчёта платежа"
            );
        }
    }
}
