package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductRequestDto;
import ru.yandex.practicum.dto.ProductResponseDto;
import ru.yandex.practicum.enums.ProductStatus;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.ProductOperationException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public List<ProductResponseDto> getAllActive() {
        try {
            List<Product> products = repository.findByStatus(ProductStatus.ACTIVE);
            return mapper.toDtoList(products);
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при получении активных продуктов", e);
        }
    }

    @Override
    public ProductResponseDto getById(UUID id) {
        Optional<Product> productOpt = repository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return mapper.toDto(productOpt.get());
    }

    @Override
    public ProductResponseDto create(ProductRequestDto dto) {
        try {
            Product product = mapper.toEntity(dto);
            Product saved = repository.save(product);
            return mapper.toDto(saved);
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при создании продукта: " + dto.getName(), e);
        }
    }

    @Override
    public ProductResponseDto update(UUID id, ProductRequestDto dto) {
        Optional<Product> productOpt = repository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        Product product = productOpt.get();
        mapper.updateEntity(product, dto);

        Product updated = repository.save(product);
        return mapper.toDto(updated);
    }

    @Override
    public void deactivate(UUID id) {
        Optional<Product> productOpt = repository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        Product product = productOpt.get();
        product.setStatus(ProductStatus.DEACTIVATE);
        repository.save(product);
    }
}
