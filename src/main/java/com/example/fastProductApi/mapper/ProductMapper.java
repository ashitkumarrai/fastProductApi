package com.example.fastProductApi.mapper;

import com.example.fastProductApi.dto.ProductRequestDto;
import com.example.fastProductApi.dto.ProductResponseDto;
import com.example.fastProductApi.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.id());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());

        return product;
    }

    public ProductResponseDto toDTO(Product entity) {
        if (entity == null) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        BigDecimal price = null;
        int stock = 0;
        LocalDateTime createdAt = null;
        LocalDateTime lastUpdatedAt = null;
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        if (entity.getStock() != null) {
            stock = entity.getStock();
        }
        createdAt = entity.getCreatedAt();
        lastUpdatedAt = entity.getLastUpdatedAt();
        ProductResponseDto productResponseDTO = new ProductResponseDto(id, name, description, price, stock, createdAt, lastUpdatedAt, null);
        return productResponseDTO;

    }
}
