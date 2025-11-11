package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetQuantityDto;
import ru.yandex.practicum.enums.ProductCategory;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

    Map<String, Object> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID id);

    ProductDto create(ProductDto dto);

    ProductDto update(ProductDto dto);

    Boolean deactivate(UUID id);

    Boolean updateQuantityState(SetQuantityDto setQuantityDto);
}
