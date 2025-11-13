package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;

public interface WarehouseService {

    void addNewProductToWarehouse(NewProductWarehouseRequestDto newProductWarehouseRequestDto);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void updateProductToWarehouse(AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    AddressDto getWarehouseAddress();
}
