package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import java.util.UUID;

public interface ProductService {

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID id);

    ProductDto create(ProductDto dto);

    ProductDto update(ProductDto dto);

    Boolean deactivate(UUID id);

    Boolean updateQuantityState(UUID productId, QuantityState quantityState);
}
