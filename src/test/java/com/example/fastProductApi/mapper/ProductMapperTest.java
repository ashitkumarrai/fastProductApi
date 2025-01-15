package com.example.fastProductApi.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.fastProductApi.dto.ProductRequestDto;
import com.example.fastProductApi.dto.ProductResponseDto;
import com.example.fastProductApi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
    }

    @Test
    void toEntity_shouldMapDtoToEntityCorrectly() {
        // Arrange
        ProductRequestDto productRequestDto = new ProductRequestDto(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                10
        );

        // Act
        Product product = productMapper.toEntity(productRequestDto);

        // Assert
        assertNotNull(product);
        assertEquals(productRequestDto.id(), product.getId());
        assertEquals(productRequestDto.name(), product.getName());
        assertEquals(productRequestDto.description(), product.getDescription());
        assertEquals(productRequestDto.price(), product.getPrice());
        assertEquals(productRequestDto.stock(), product.getStock());
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        // Act
        Product product = productMapper.toEntity(null);

        // Assert
        assertNull(product);
    }

    @Test
    void toDTO_shouldMapEntityToDtoCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(10);
        product.setCreatedAt(now);
        product.setLastUpdatedAt(now);

        // Act
        ProductResponseDto productResponseDto = productMapper.toDTO(product);

        // Assert
        assertNotNull(productResponseDto);
        assertEquals(product.getId(), productResponseDto.getId());
        assertEquals(product.getName(), productResponseDto.getName());
        assertEquals(product.getDescription(), productResponseDto.getDescription());
        assertEquals(product.getPrice(), productResponseDto.getPrice());
        assertEquals(product.getStock(), productResponseDto.getStock());
        assertEquals(product.getCreatedAt(), productResponseDto.getCreatedAt());
        assertEquals(product.getLastUpdatedAt(), productResponseDto.getLastUpdatedAt());
    }

    @Test
    void toDTO_shouldReturnNullWhenEntityIsNull() {
        // Act
        ProductResponseDto productResponseDto = productMapper.toDTO(null);

        // Assert
        assertNull(productResponseDto);
    }

    @Test
    void toDTO_shouldHandleNullStockGracefully() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(null); // Stock is null
        product.setCreatedAt(now);
        product.setLastUpdatedAt(now);

        // Act
        ProductResponseDto productResponseDto = productMapper.toDTO(product);

        // Assert
        assertNotNull(productResponseDto);
        assertEquals(0, productResponseDto.getStock()); // Default stock should be 0
    }
}
