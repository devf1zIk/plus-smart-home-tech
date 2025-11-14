package ru.yandex.practicum.client;

import ru.yandex.practicum.dto.*;

public interface WarehousOperations {

    void addNewProductToWarehouse(NewProductWarehouseRequestDto newProductWarehouseRequestDto);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void updateProductToWarehouse(AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    AddressDto getWarehouseAddress();
}
