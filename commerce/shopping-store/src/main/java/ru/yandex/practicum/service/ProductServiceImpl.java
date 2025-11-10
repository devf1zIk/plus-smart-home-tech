package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.ProductOperationException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        try {
            Page<Product> products;
            if (category != null) {
                products = repository.findByProductStateAndProductCategory(ProductState.ACTIVE, category, pageable);
            } else {
                products = repository.findByProductState(ProductState.ACTIVE, pageable);
            }
            return products.map(mapper::toDto);
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при получении списка продуктов", e);
        }
    }

    @Override
    public ProductDto getProductById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));
        return mapper.toDto(product);
    }

    @Override
    public ProductDto create(ProductDto dto) {
        try {
            Product product = mapper.toEntity(dto);
            product.setProductState(ProductState.ACTIVE);
            Product saved = repository.save(product);
            return mapper.toDto(saved);
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при создании продукта: " + dto.getProductName(), e);
        }
    }

    @Override
    public ProductDto update(UUID id, ProductDto dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));

        mapper.updateEntity(product, dto);
        Product updated = repository.save(product);
        return mapper.toDto(updated);
    }

    @Override
    public Boolean deactivate(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));

        product.setProductState(ProductState.DEACTIVATED);
        repository.save(product);
        return true;
    }

    @Override
    public Boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + productId));

        product.setQuantityState(quantityState);
        repository.save(product);
        return true;
    }
}
