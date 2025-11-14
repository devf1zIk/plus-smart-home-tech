package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setImageSrc(dto.getImageSrc());
        product.setQuantityState(dto.getQuantityState());
        product.setProductCategory(dto.getProductCategory());
        product.setPrice(dto.getPrice());
        product.setProductState(ProductState.ACTIVE);

        return product;
    }

    public void updateEntity(Product product, ProductDto dto) {
        if (product == null || dto == null) {
            return;
        }

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setImageSrc(dto.getImageSrc());
        product.setQuantityState(dto.getQuantityState());
        product.setProductCategory(dto.getProductCategory());
        product.setPrice(dto.getPrice());
    }

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setImageSrc(product.getImageSrc());
        dto.setQuantityState(product.getQuantityState());
        dto.setProductCategory(product.getProductCategory());
        dto.setPrice(product.getPrice());
        dto.setProductState(product.getProductState());

        return dto;
    }
}
