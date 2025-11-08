package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.ProductRequestDto;
import ru.yandex.practicum.dto.ProductResponseDto;
import ru.yandex.practicum.service.ProductService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService service;

    @GetMapping
    public List<ProductResponseDto> getAll() {
        return service.getAllActive();
    }

    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public ProductResponseDto create(@Valid @RequestBody ProductRequestDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(@PathVariable UUID id, @Valid @RequestBody ProductRequestDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable UUID id) {
        service.deactivate(id);
    }
}
