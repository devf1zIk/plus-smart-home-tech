package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    Page<ProductDto> getProducts(@RequestParam(value = "category") ProductCategory category,
                                 Pageable pageable);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @PostMapping("/api/v1/shopping-store")
    ProductDto createProduct(@RequestBody @Valid ProductDto dto);

    @PutMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody @Valid ProductDto dto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    Boolean deleteProduct(@RequestBody @NotNull UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean updateQuantityState(@RequestParam @NotNull UUID productId,
                                @RequestParam @NotNull QuantityState quantityState);
}
