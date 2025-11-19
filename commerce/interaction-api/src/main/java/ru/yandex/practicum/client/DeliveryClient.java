package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryRequestDto;
import ru.yandex.practicum.dto.order.OrderDto;
import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto create(@RequestBody NewDeliveryRequestDto dto);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal cost(@RequestBody OrderDto dto);

    @PostMapping("/api/v1/delivery/picked")
    DeliveryDto picked(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/successful")
    DeliveryDto successful(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    DeliveryDto failed(@RequestBody UUID orderId);

}
