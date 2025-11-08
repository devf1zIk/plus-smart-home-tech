package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ProductRequestDto;
import ru.yandex.practicum.dto.ProductResponseDto;
import ru.yandex.practicum.enums.ProductStatus;
import ru.yandex.practicum.model.Product;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setAvailability(dto.getAvailability());
        product.setPrice(dto.getPrice());
        product.setStatus(ProductStatus.ACTIVE);

        return product;
    }

    public void updateEntity(Product product, ProductRequestDto dto) {
        if (product == null || dto == null) {
            return;
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setAvailability(dto.getAvailability());
        product.setPrice(dto.getPrice());
    }

    public ProductResponseDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setAvailability(product.getAvailability());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus());

        return dto;
    }

    public List<ProductResponseDto> toDtoList(List<Product> products) {
        if (products == null) {
            return new ArrayList<>();
        }

        List<ProductResponseDto> list = new ArrayList<>();
        for (Product p : products) {
            list.add(toDto(p));
        }
        return list;
    }
}
