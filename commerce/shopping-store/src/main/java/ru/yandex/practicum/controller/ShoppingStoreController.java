package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.service.ProductService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService productService;

    @GetMapping
    @Override
    public Page<ProductDto> getProducts(@RequestParam(value = "category", required = false) ProductCategory category,
                                        Pageable pageable) {
        return productService.getProducts(category, pageable);
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto dto) {
        return productService.create(dto);
    }

    @Override
    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto dto) {
        return productService.update(dto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody @NotNull UUID productId) {
        return productService.deactivate(productId);
    }

    @Override
    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam @NotNull UUID productId,
                                       @RequestParam @NotNull QuantityState quantityState) {
        return productService.updateQuantityState(productId,quantityState);
    }
}
