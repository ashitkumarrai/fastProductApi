package com.example.fastProductApi.service;

import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductServiceForBasicCrud {
    @Autowired
    private ProductRepository productRepository;

    // Delete product and evict from cache
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @CachePut(value = "products", key = "#result.id")
    public Product saveProduct(Product product) {
        if (product != null) {
            return productRepository.save(product);
        }
        return null;
    }

    @CachePut(value = "products", key = "#result.id")
    public Product updateProduct(Product product) {
        if (product != null && product.getId() == null) {
            // If product doesn't have an ID, it's a new product, so save it
            product.setCreatedAt(LocalDateTime.now());
            product.setLastUpdatedAt(LocalDateTime.now());
            return productRepository.save(product);
        } else {
            // If product has an ID, check if it exists
            Optional<Product> existingProduct = productRepository.findById(product.getId());

            // If product exists, update it; otherwise, return null
            if (existingProduct.isPresent()) {
                Product productToUpdate = existingProduct.get();
                productToUpdate.setName(product.getName());
                productToUpdate.setPrice(product.getPrice());
                productToUpdate.setStock(product.getStock());
                productToUpdate.setDescription(product.getDescription());
                productToUpdate.setLastUpdatedAt(LocalDateTime.now());

                return productRepository.save(productToUpdate);  // Save updated product
            } else {
                // Return null if the product is not found
                return null;
            }
        }
    }

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}
