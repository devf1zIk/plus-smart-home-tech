package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetQuantityDto;
import ru.yandex.practicum.enums.ProductCategory;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    Page<ProductDto> getProducts(@RequestParam("category") ProductCategory category,
                                 Pageable pageable);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @PostMapping("/api/v1/shopping-store")
    ProductDto createProduct(@RequestBody ProductDto dto);

    @PutMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody ProductDto dto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    Boolean deleteProduct(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean updateQuantityState(@RequestBody SetQuantityDto  setQuantityDto);
}
