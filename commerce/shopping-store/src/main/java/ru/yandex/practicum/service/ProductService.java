package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ProductRequestDto;
import ru.yandex.practicum.dto.ProductResponseDto;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductResponseDto> getAllActive();

    ProductResponseDto getById(UUID id);

    ProductResponseDto create(ProductRequestDto dto);

    ProductResponseDto update(UUID id, ProductRequestDto dto);

    void deactivate(UUID id);
}
