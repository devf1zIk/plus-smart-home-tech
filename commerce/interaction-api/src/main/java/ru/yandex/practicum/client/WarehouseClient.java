package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PutMapping("/api/v1/warehouse")
    void ProductToWarehouse(@RequestBody @Valid NewProductWarehouseRequestDto newProductWarehouseRequestDto);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/api/v1/warehouse/add")
    void updateProductToWarehouse(@RequestBody @Valid ProductWarehouseRequestDto productWarehouseRequestDto);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();
}
