package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        try {
            Page<Product> products;
            if (category != null) {
                products = productRepository.findByProductStateAndProductCategory(ProductState.ACTIVE, category, pageable);
            } else {
                products = productRepository.findByProductState(ProductState.ACTIVE, pageable);
            }

            List<ProductDto> content = products.getContent().stream()
                    .map(productMapper::toDto)
                    .toList();

            return new PageImpl<>(content, pageable, products.getTotalElements());
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при получении списка продуктов");
        }
    }

    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));
        return productMapper.toDto(product);
    }

    public ProductDto create(ProductDto dto) {
        try {
            Product product = productMapper.toEntity(dto);
            product.setProductState(ProductState.ACTIVE);
            Product saved = productRepository.save(product);
            return productMapper.toDto(saved);
        } catch (Exception e) {
            throw new ProductOperationException("Ошибка при создании продукта: " + dto.getProductName());
        }
    }

    public ProductDto update(ProductDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + dto.getProductId()));

        productMapper.updateEntity(product, dto);
        Product updated = productRepository.save(product);
        return productMapper.toDto(updated);
    }

    public Boolean deactivate(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));

        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    public Boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + productId));

        product.setQuantityState(quantityState);
        productRepository.save(product);
        return true;
    }
}
