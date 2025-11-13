package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    public void addProductToWarehouse(@RequestBody @Valid NewProductWarehouseRequestDto newProductWarehouseRequestDto) {
        warehouseService.addNewProductToWarehouse(newProductWarehouseRequestDto);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto ShoppingCartDto) {
        return warehouseService.checkProductQuantityInWarehouse(ShoppingCartDto);
    }

    @Override
    @PostMapping("/add")
    public void updateProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {
        warehouseService.updateProductToWarehouse(addProductToWarehouseRequestDto);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}
