package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductRequestDto;
import ru.yandex.practicum.dto.ProductResponseDto;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    List<ProductResponseDto> getAll();

    @GetMapping("/api/v1/shopping-store/{id}")
    ProductResponseDto getById(@PathVariable UUID id);

    @PostMapping("/api/v1/shopping-store")
    ProductResponseDto create(@RequestBody ProductRequestDto dto);

    @PutMapping("/api/v1/shopping-store/{id}")
    ProductResponseDto update(@PathVariable UUID id, @RequestBody ProductRequestDto dto);

    @DeleteMapping("/api/v1/shopping-store/{id}")
    void deactivate(@PathVariable UUID id);
}
