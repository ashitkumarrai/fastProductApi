package com.example.fastProductApi.service;

import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.exception.CustomException;
import com.example.fastProductApi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
class ProductServiceForBasicCrudTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ProductServiceForBasicCrud productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(10);
        product.setCreatedAt(LocalDateTime.now());
        product.setLastUpdatedAt(LocalDateTime.now());
    }

    @Test
    void saveProduct_shouldSaveProductAndPutIntoCache() throws CustomException {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product savedProduct = productService.saveProduct(product);

        // Assert
        assertNotNull(savedProduct);
        assertEquals(product.getId(), savedProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void saveProduct_shouldThrowCustomExceptionWhenErrorOccurs() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        CustomException thrown = assertThrows(CustomException.class, () -> productService.saveProduct(product));
        assertTrue(thrown.getMessage().contains("Database error"));
    }

    @Test
    void updateProduct_shouldUpdateProductAndPutIntoCache() throws CustomException {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        product.setPrice(BigDecimal.valueOf(199.99)); // Changing price for update test

        // Act
        Product updatedProduct = productService.updateProduct(product);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(product.getId(), updatedProduct.getId());
        assertEquals(BigDecimal.valueOf(199.99), updatedProduct.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldReturnNullWhenProductNotFound() throws CustomException {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Product updatedProduct = productService.updateProduct(product);

        // Assert
        assertNull(updatedProduct);
    }

    @Test
    void deleteProduct_shouldDeleteProductAndEvictFromCache() throws CustomException {
        // Act
        productService.deleteProduct(product);

        // Assert
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_shouldThrowCustomExceptionWhenErrorOccurs() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(productRepository).delete(any(Product.class));

        // Act & Assert
        CustomException thrown = assertThrows(CustomException.class, () -> productService.deleteProduct(product));
        assertTrue(thrown.getMessage().contains("Database error"));
    }


    @Test
    void getProductById_shouldReturnEmptyWhenNotFound() throws CustomException {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Product> foundProduct = productService.getProductById(1L);

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void getProductById_shouldThrowCustomExceptionWhenErrorOccurs() {
        // Arrange
        when(productRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        CustomException thrown = assertThrows(CustomException.class, () -> productService.getProductById(1L));
        assertTrue(thrown.getMessage().contains("Database error"));
    }

}
