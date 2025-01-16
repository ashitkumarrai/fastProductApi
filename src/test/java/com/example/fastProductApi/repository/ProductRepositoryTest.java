package com.example.fastProductApi.repository;

import com.example.fastProductApi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Prepare a sample product for testing
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setStock(10);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setLastUpdatedAt(LocalDateTime.now());

        // Save the product in the database
        productRepository.save(testProduct);
    }

    @Test
    void saveProduct_shouldSaveAndReturnProduct() {
        // Act
        Product savedProduct = productRepository.save(testProduct);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getDescription(), savedProduct.getDescription());
    }

    @Test
    void findById_shouldReturnProductWhenExists() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(testProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(testProduct.getName(), foundProduct.get().getName());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(999L); // Non-existing ID

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void deleteById_shouldRemoveProduct() {
        // Act
        productRepository.deleteById(testProduct.getId());
        Optional<Product> foundProduct = productRepository.findById(testProduct.getId());

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        // Act
        Iterable<Product> products = productRepository.findAll();

        // Assert
        assertNotNull(products);
        assertTrue(products.iterator().hasNext());
    }
}
