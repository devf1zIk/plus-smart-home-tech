package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetQuantityDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.service.ProductService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService service;

    @Override
    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        Pageable pageable) {
        return service.getProducts(category, pageable);
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable @NotNull UUID productId) {
        return service.getProductById(productId);
    }

    @Override
    @PutMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{productId}")
    public ProductDto updateProduct(@PathVariable @NotNull UUID productId,
                                    @Valid @RequestBody ProductDto dto) {
        return service.update(productId, dto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody @NotNull UUID productId) {
        return service.deactivate(productId);
    }

    @Override
    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam @NotNull SetQuantityDto setQuantityDto) {
        return service.updateQuantityState(setQuantityDto);
    }
}
