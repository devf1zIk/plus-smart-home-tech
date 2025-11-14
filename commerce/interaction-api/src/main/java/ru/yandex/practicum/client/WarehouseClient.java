package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehousOperations {

    @Override
    @PutMapping("/api/v1/warehouse")
    void addNewProductToWarehouse(@RequestBody @Valid NewProductWarehouseRequestDto newProductWarehouseRequestDto);

    @Override
    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @Override
    @PostMapping("/api/v1/warehouse/add")
    void updateProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    @Override
    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();
}
