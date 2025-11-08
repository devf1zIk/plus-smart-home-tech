package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.CartItemResponseDto;
import ru.yandex.practicum.model.CartItem;

@Component
public class CartItemMapper {

    public CartItemResponseDto toDto(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}
